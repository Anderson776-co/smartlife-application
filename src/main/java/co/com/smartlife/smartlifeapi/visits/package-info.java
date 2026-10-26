@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "shared::mapper", "shared::repository", "crosscutting::helper,",
        "crosscutting::session", "crosscutting::exception", "shared::messagecatalog-port"
    }
)
package co.com.smartlife.smartlifeapi.visits;