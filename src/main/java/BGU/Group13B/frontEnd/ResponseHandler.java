package BGU.Group13B.frontEnd;

import BGU.Group13B.service.Response;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.util.Optional;

public interface ResponseHandler {

    /**
     * Handles a response from service.
     * @param response              the response to handle
     * @param navInCaseOfFailure    the navigation to perform in case of failure
     * @return                      the data from the response (if succeeded)
     */
    default <T> T handleResponse(Response<T> response, String navInCaseOfFailure) {
        if (response.didntSucceed()) {
            notifyError("Error: " + response.getMessage());
            navigate(navInCaseOfFailure);
            return null;
        }
        return response.getData();
    }

    /**
     * Handles a response from service, without navigating in case of failure.
     * @param response              the response to handle
     * @return                      the data from the response (if succeeded)
     */
    default <T> T handleResponse(Response<T> response) {
        if (response.didntSucceed()) {
            notifyError("Error: " + response.getMessage());
            return null;
        }
        return response.getData();
    }

    /**
     * Handles a response from service, without navigating in case of failure.
     * @param response              the response to handle
     * @return                      the data as optional
     */
    default <T> Optional<T> handleOptionalResponse(Response<T> response) {
        if (response.didntSucceed()) {
            notifyError("Error: " + response.getMessage());
            return Optional.empty();
        }
        return Optional.of(response.getData());
    }

    /**
     * Navigates to a given path.
     * @param nav   the path to navigate to
     */
    default void navigate(String nav) {
        UI.getCurrent().navigate(nav);
    }


    /**
     * Shows a success notification to the user.
     * @param message   the message to show
     */
    default void notifySuccess(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    /**
     * Shows an error notification to the user.
     * @param message   the message to show
     */
    default void notifyError(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    /**
     * Shows a warning notification to the user.
     * @param message   the message to show
     */
    default void notifyWarning(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    /**
     * Shows an info notification to the user.
     * @param message   the message to show
     */
    default void notifyInfo(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}
