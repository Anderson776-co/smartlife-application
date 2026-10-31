@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "shared::mapper", "shared::repository", "shared::usecase", "shared::input-port",
        "shared::response", "shared::messagecatalog-port", 
        "crosscutting::helper", "crosscutting::session", "crosscutting::exception",
        "crosscutting::specification-generics", "crosscutting::rules"
    }
)
package co.com.smartlife.smartlifeapi.visits;