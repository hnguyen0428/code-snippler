import {ALERT_ACTIVE, ALERT_INACTIVE, BINARY_ALERT_ACTIVE, BINARY_ALERT_INACTIVE} from './types';


export const showAlert = (title, message) => dispatch => {
    dispatch({
        type: ALERT_ACTIVE,
        payload: {
            title: title,
            message: message
        }
    });
};


export const closeAlert = () => dispatch => {
    dispatch({
        type: ALERT_INACTIVE
    });
};


export const showBinaryAlert = (title, message, actionOne, actionTwo) => dispatch => {
    let closeBinaryAlert = () => {
        dispatch({type: BINARY_ALERT_INACTIVE});
    };

    actionOne = actionOne || {};
    actionTwo = actionTwo || {};

    // Default action is to close binary alert
    actionOne.callback = actionOne.callback ? actionOne.callback : closeBinaryAlert;
    actionTwo.callback = actionTwo.callback ? actionTwo.callback : closeBinaryAlert;

    actionOne.title = actionOne.title ? actionOne.title : 'No';
    actionTwo.title = actionTwo.title ? actionTwo.title : 'Yes';

    dispatch({
        type: BINARY_ALERT_ACTIVE,
        payload: {
            title: title,
            message: message,
            actionOne: actionOne.callback,
            actionTwo: actionTwo.callback,
            actionOneTitle: actionOne.title,
            actionTwoTitle: actionTwo.title
        }
    });
};


export const closeBinaryAlert = () => dispatch => {
    dispatch({
        type: BINARY_ALERT_INACTIVE
    });
};