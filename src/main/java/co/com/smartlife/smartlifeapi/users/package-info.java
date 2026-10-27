@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared::repository", "shared::mapper",
    		"shared::usecase", "shared::infrastructure", "crosscutting::exception", 
    		"crosscutting::session", "shared::messagecatalog-port", "shared::response",
    		"crosscutting::helper", "shared::validator", "shared::input-port"}
)
package co.com.smartlife.smartlifeapi.users;