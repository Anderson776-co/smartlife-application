@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "shared::repository",
        "shared::mapper",
        "shared::usecase",
        "shared::infrastructure",
        "crosscutting::exception",
        "crosscutting::session",
        "shared::messagecatalog-port",
        "shared::response",
        "crosscutting::helper",
        "shared::input-port",
        "shared::event-publisher",
        "shared::view-refresher",
        "urbanization::events"
    }
)
package co.com.smartlife.smartlifeapi.users;
