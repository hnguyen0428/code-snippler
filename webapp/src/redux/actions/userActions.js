import Snippler from '../../api/SnipplerClient';
import {FETCH_USER, FETCH_ME, FETCH_SNIPPETS} from './types';
import {handleActionsResult} from './actions';


export const updateProfile = (params, callback, noResCB) => dispatch => {
    Snippler.user().updateProfile(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            let userData = res.data;
            dispatch({
                type: FETCH_ME,
                payload: userData
            });
        }
    });
};


export const fetchMe = (params, callback, noResCB) => dispatch => {
    Snippler.user().getMe(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            let userData = res.data;
            dispatch({
                type: FETCH_ME,
                payload: userData
            });
            // If showSnippetDetails then dispatch the snippets data too
            if (params && params.showSnippetDetails) {
                let snippets = {...userData.createdSnippets, ...userData.savedSnippets};
                dispatch({
                    type: FETCH_SNIPPETS,
                    payload: snippets
                });
            }
        }
    });
};


export const fetchUser = (userId, params, callback, noResCB) => dispatch => {
    Snippler.user().getUser(userId, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            let userData = res.data;

            if (params && params.showSnippetDetails) {
                let snippets = {...userData.createdSnippets, ...userData.savedSnippets};
                dispatch({
                    type: FETCH_SNIPPETS,
                    payload: snippets
                });

                // Make sure that user's createdSnippets and savedSnippets only contain IDs
                userData.createdSnippets = userData.createdSnippets.map(snippet => {return snippet.snippetId;});
                userData.savedSnippets = userData.savedSnippets.map(snippet => {return snippet.snippetId;});
                dispatch({
                    type: FETCH_USER,
                    payload: userData
                });
            }
            else {
                dispatch({
                    type: FETCH_USER,
                    payload: userData
                });
            }
        }
    });
};


export const fetchMySavedSnippets = (params, callback, noResCB) => dispatch => {
    Snippler.user().getMySavedSnippets(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            if (params && params.showDetails) {
                dispatch({type: FETCH_SNIPPETS, payload: res.data});
            }
        }
    });
};


export const fetchUserSavedSnippets = (userId, params, callback, noResCB) => dispatch => {
    Snippler.user().getUserSavedSnippets(userId, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            if (params && params.showDetails) {
                dispatch({type: FETCH_SNIPPETS, payload: res.data});
            }
        }
    });
};


export const fetchMyCreatedSnippets = (params, callback, noResCB) => dispatch => {
    Snippler.user().getMyCreatedSnippets(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            if (params && params.showDetails) {
                dispatch({type: FETCH_SNIPPETS, payload: res.data});
            }
        }
    });
};


export const fetchUserCreatedSnippets = (userId, params, callback, noResCB) => dispatch => {
    Snippler.user().getUserCreatedSnippets(userId, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            if (params && params.showDetails) {
                dispatch({type: FETCH_SNIPPETS, payload: res.data});
            }
        }
    });
};