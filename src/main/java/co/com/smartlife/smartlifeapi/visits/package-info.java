@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "shared::mapper", "shared::repository", "shared::usecase", "shared::input-port",
        "shared::validator", "shared::response", "shared::messagecatalog-port", 
        "crosscutting::helper", "  crosscutting::session", "crosscutting::exception",
    }
)
package co.com.smartlife.smartlifeapi.visits;