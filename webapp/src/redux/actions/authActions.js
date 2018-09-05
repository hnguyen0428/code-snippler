import Snippler from '../../api/SnipplerClient';
import {LOGIN, REGISTER, LOGGED_OUT, ALERT_ACTIVE} from './types';
import {handleActionsResult} from './actions';


export const login = (username, password, callback, noResCB) => dispatch => {
    Snippler.auth().login({
        username: username,
        password: password,
    }, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB, false);
        if (success)
            dispatch({
                type: LOGIN,
                payload: res.data
            });
        else if (error && error.response) {
            dispatch({
                type: ALERT_ACTIVE,
                payload: {
                    title: 'Error',
                    message: 'Invalid username or password'
                }
            });
        }
    });
};


export const register = (username, password, callback, noResCB) => dispatch => {
    Snippler.auth().register({
        username: username,
        password: password,
    }, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success)
            dispatch({
                type: REGISTER,
                payload: res.data
            });
    });
};


export const changePassword = (currPw, newPw, callback, noResCB) => dispatch => {
    Snippler.auth().changePassword({
        currentPassword: currPw,
        newPassword: newPw,
    }, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
    });
};


export const logout = () => dispatch => {
    let apiKey = localStorage.getItem('apiKey');

    // Remove the user object associated with this key first
    if (apiKey) {
        localStorage.removeItem(apiKey);
        localStorage.removeItem('apiKey');
        Snippler.setApiKey('');
        dispatch({
            type: LOGGED_OUT
        });
    }
};