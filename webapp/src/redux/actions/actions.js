import store from '../store';
import {ALERT_ACTIVE} from './types';


// This callback gives back the JSON, not the user data
export const handleActionsResult = (data, error, callback, noResCB, showErr) => {
    showErr = showErr === undefined ? true : showErr;

    if (data) {
        if (callback)
            callback(data, null);

        return true;
    }
    else if (error) {
        if (error.response) {
            // Able to reach the server and server responds with a bad request code
            if (callback) {
                callback(null, error.response.data);

                if (showErr)
                    store.dispatch({
                        type: ALERT_ACTIVE,
                        payload: {
                            title: 'Error',
                            message: error.response.data.error.message
                        }
                    });
            }
        }
        else {
            // Unable to get a response from server
            console.log(error);
            if (noResCB)
                noResCB(error);

            store.dispatch({
                type: ALERT_ACTIVE,
                payload: {
                    title: 'Error',
                    message: 'Unable to get a response from server'
                }
            });
        }
    }
    return false;
};