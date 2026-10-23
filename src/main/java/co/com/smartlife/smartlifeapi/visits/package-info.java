@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "shared::mapper", "shared::repository", "crosscutting::helper"
    }
)
package co.com.smartlife.smartlifeapi.visits;