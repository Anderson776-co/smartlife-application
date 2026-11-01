// Carga y verifica el catálogo de Strapi (Node 18+).
//
//   Cargar (token FULL ACCESS temporal):
//     STRAPI_URL=https://... STRAPI_TOKEN=... node strapi-catalog.mjs load [es]
//   Verificar con el token de SOLO LECTURA, igual que lo consulta el backend:
//     STRAPI_URL=https://... STRAPI_TOKEN=... node strapi-catalog.mjs check [es]
//
// "load" crea o actualiza cada entrada (idempotente). Después hay que publicarlas
// en el admin (Content Manager > seleccionar todo > Publish) si quedaron en borrador.
// "check" solo ve lo PUBLICADO, por eso detecta entradas en borrador.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const dir = path.dirname(fileURLToPath(import.meta.url));
const [mode = 'check', locale = 'es'] = process.argv.slice(2);
const base = (process.env.STRAPI_URL ?? '').replace(/\/+$/, '');
const token = process.env.STRAPI_TOKEN ?? '';
if (!base || !token) { console.error('Faltan STRAPI_URL y STRAPI_TOKEN'); process.exit(2); }

const H = { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` };
const sets = [
  { collection: 'message-catalogs', file: `message-catalog.${locale}.json`, field: 'content' },
  { collection: 'notification-catalogs', file: `notification-catalog.${locale}.json`, field: 'content' },
];

async function find(collection, code) {
  const url = `${base}/api/${collection}?filters[code][$eq]=${encodeURIComponent(code)}&locale=${locale}`;
  const res = await fetch(url, { headers: H, signal: AbortSignal.timeout(15000) });
  if (!res.ok) throw new Error(`${res.status} ${res.statusText} en ${collection}`);
  return (await res.json()).data?.[0];
}

let missing = 0, failed = 0;
for (const { collection, file } of sets) {
  const items = JSON.parse(fs.readFileSync(path.join(dir, file), 'utf8'));
  for (const item of items) {
    try {
      const found = await find(collection, item.code);
      if (mode === 'check') {
        if (!found || !found.content) { missing++; console.log(`FALTA  ${collection} ${item.code}`); }
        continue;
      }
      const url = found
        ? `${base}/api/${collection}/${found.documentId}?locale=${locale}`
        : `${base}/api/${collection}?locale=${locale}`;
      const res = await fetch(url, { method: found ? 'PUT' : 'POST', headers: H, body: JSON.stringify({ data: item }) });
      console.log(res.ok ? 'OK    ' : `ERROR ${res.status}`, collection, item.code);
      if (!res.ok) failed++;
    } catch (e) { failed++; console.log('ERROR ', collection, item.code, e.message); }
  }
}
if (mode === 'check') console.log(missing || failed ? `\nProblemas: ${missing} faltantes, ${failed} errores` : '\nTodo el catálogo responde publicado.');
process.exit(missing || failed ? 1 : 0);
