package co.com.smartlife.smartlifeapi.shared.repository.criteria;

public interface CriteriaCountRepository {

	/** Cuenta todos los registros que cumplen los filtros, sin aplicar limit ni offset. */
	long count(Criteria criteria);

}
