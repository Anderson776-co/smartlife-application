@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "shared::repository",
        "shared::mapper",
        "shared::usecase",
        "shared::input-port",
        "shared::response",
        "shared::messagecatalog-port",
        "shared::event-publisher",
        "crosscutting::exception",
        "crosscutting::helper"
    }
)
package co.com.smartlife.smartlifeapi.urbanization;
