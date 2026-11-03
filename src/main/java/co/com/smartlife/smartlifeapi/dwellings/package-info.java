@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "shared::repository",
        "shared::mapper",
        "shared::usecase",
        "shared::input-port",
        "shared::response",
        "shared::messagecatalog-port",
        "shared::event-publisher",
        "shared::view-refresher",
        "crosscutting::exception",
        "crosscutting::helper",
        "users::events",
        "urbanization::events"
    }
)
package co.com.smartlife.smartlifeapi.dwellings;
