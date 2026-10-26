@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"shared::repository", "shared::mapper",
    		"shared::usecase", "shared::infrastructure", "crosscutting::exception", 
    		"crosscutting::session", "shared::messagecatalog"}
)
package co.com.smartlife.smartlifeapi.users;