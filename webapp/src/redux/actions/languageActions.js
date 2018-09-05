import Snippler from '../../api/SnipplerClient';
import {FETCH_ALL_LANGUAGES} from './types';
import {handleActionsResult} from './actions';


export const fetchAllLanguages = (callback, noResCB) => dispatch => {
    Snippler.language().getAllLanguages((res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success)
            dispatch({type: FETCH_ALL_LANGUAGES, payload: res.data});
    });
};