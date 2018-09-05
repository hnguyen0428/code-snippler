import {RESET_OVERRIDE_PATH, OVERRIDE_PATH} from './types';


export const overridePath = (pathname) => dispatch => {
    dispatch({
        type: OVERRIDE_PATH,
        payload: {
            pathname: pathname
        }
    });
};


export const resetOverridePath = () => dispatch => {
    dispatch({
        type: RESET_OVERRIDE_PATH
    });
};