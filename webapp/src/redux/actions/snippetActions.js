import Snippler from '../../api/SnipplerClient';
import {
    CREATE_SNIPPET, FETCH_SNIPPET, UPDATE_SNIPPET, DELETE_SNIPPET, UPVOTE_SNIPPET, DOWNVOTE_SNIPPET,
    SAVE_SNIPPET, FETCH_POPULAR, FETCH_MOST_VIEWS, FETCH_MOST_SAVED, FETCH_MOST_UPVOTES,
    FETCH_COMMENTS, FETCH_USER, CREATE_COMMENT, FETCH_SNIPPETS, FETCH_USERS, SHOULD_INCREASE_VIEW,
    RESET_INCREASE_VIEW, SEARCH_SNIPPETS
} from '../actions/types';
import {handleActionsResult} from './actions';


export const createSnippet = (params, callback, noResCB) => dispatch => {
    Snippler.snippet().createSnippet(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: CREATE_SNIPPET,
                payload: res.data
            });
        }
    });
};


export const updateSnippet = (snippetId, params, callback, noResCB) => dispatch => {
    Snippler.snippet().updateSnippet(snippetId, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: UPDATE_SNIPPET,
                payload: res.data
            });
        }
    });
};


export const fetchSnippet = (snippetId, params, callback, noResCB) => dispatch => {
    Snippler.snippet().getSnippet(snippetId, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: FETCH_SNIPPET,
                payload: res.data
            });

            if (params && params.showCommentDetails) {
                let comments = res.data.comments;
                dispatch({
                    type: FETCH_COMMENTS,
                    payload: comments
                });
            }
            if (params && params.showUserDetails) {
                let user = res.data.user;
                dispatch({
                    type: FETCH_USER,
                    payload: user
                });
            }
        }
    });
};


export const fetchSnippetsByIds = (snippetIds, params, callback, noResCB) => dispatch => {
    Snippler.snippet().getSnippets(snippetIds, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: FETCH_SNIPPETS,
                payload: res.data
            });

            if (params && params.showUserDetails) {
                let users = [];
                res.data.forEach(snippet => {
                    users.push(snippet.user);
                });

                dispatch({
                    type: FETCH_USERS,
                    payload: users
                });
            }
        }
    });
};


export const fetchSnippetsByLanguage = (language, params, callback, noResCB) => dispatch => {
    Snippler.snippet().getSnippetsByLanguage(language, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: FETCH_SNIPPETS,
                payload: res.data
            });
        }
    });
};


export const deleteSnippet = (snippetId, callback, noResCB) => dispatch => {
    Snippler.snippet().deleteSnippet(snippetId, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: DELETE_SNIPPET,
                payload: snippetId
            });
        }
    });
};


export const upvoteSnippet = (snippetId, upvote, callback, noResCB) => dispatch => {
    Snippler.snippet().upvoteSnippet(snippetId, upvote, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: UPVOTE_SNIPPET,
                payload: {snippetId: snippetId, upvoted: upvote}
            });
        }
    });
};


export const downvoteSnippet = (snippetId, downvote, callback, noResCB) => dispatch => {
    Snippler.snippet().downvoteSnippet(snippetId, downvote, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: DOWNVOTE_SNIPPET,
                payload: {snippetId: snippetId, downvoted: downvote}
            });
        }
    });
};


export const saveSnippet = (snippetId, save, callback, noResCB) => dispatch => {
    Snippler.snippet().saveSnippet(snippetId, save, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: SAVE_SNIPPET,
                payload: {snippetId: snippetId, saved: save}
            });
        }
    });
};


export const createComment = (snippetId, params, callback, noResCB) => dispatch => {
    Snippler.snippet().createComment(snippetId, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: CREATE_COMMENT,
                payload: res.data
            });
        }
    });
};


export const fetchComments = (snippetId, params, callback, noResCB) => dispatch => {
    Snippler.snippet().getComments(snippetId, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);

        if (success) {
            dispatch({
                type: FETCH_COMMENTS,
                payload: res.data
            });


            if (params && params.showUserDetails) {
                let users = [];
                res.data.forEach(comment => {
                    users.push(comment.user);
                });

                if (users.length !== 0)
                    dispatch({
                        type: FETCH_USERS,
                        payload: users
                    });
            }
        }
    });
};


export const searchSnippets = (params, callback, noResCB) => dispatch => {
    params.query = params.query ? params.query : '';
    params = {...params, languages: Snippler.constructLanguagesFilter()};

    Snippler.snippet().searchSnippets(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: FETCH_SNIPPETS,
                payload: res.data
            });

            dispatch({
                type: SEARCH_SNIPPETS,
                payload: {
                    values: res.data,
                    query: params.query
                }
            });
        }
    });
};


export const fetchRecentSnippets = (params, callback, noResCB) => dispatch => {
    if (params)
        params = {...params, languages: Snippler.constructLanguagesFilter()};
    else
        params = {languages: Snippler.constructLanguagesFilter()};

    Snippler.snippet().getRecent(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({type: FETCH_SNIPPETS, payload: res.data});
        }
    });
};


export const fetchPopularSnippets = (params, callback, noResCB) => dispatch => {
    if (params)
        params = {...params, languages: Snippler.constructLanguagesFilter()};
    else
        params = {languages: Snippler.constructLanguagesFilter()};

    Snippler.snippet().getPopular(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({type: FETCH_POPULAR, payload: res.data});
        }
    });
};


export const fetchMostViewsSnippets = (params, callback, noResCB) => dispatch => {
    if (params)
        params = {...params, languages: Snippler.constructLanguagesFilter()};
    else
        params = {languages: Snippler.constructLanguagesFilter()};

    Snippler.snippet().getMostViews(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({type: FETCH_MOST_VIEWS, payload: res.data});
        }
    });
};


export const fetchMostUpvotesSnippets = (params, callback, noResCB) => dispatch => {
    if (params)
        params = {...params, languages: Snippler.constructLanguagesFilter()};
    else
        params = {languages: Snippler.constructLanguagesFilter()};

    Snippler.snippet().getMostUpvotes(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({type: FETCH_MOST_UPVOTES, payload: res.data});
        }
    });
};


export const fetchMostSavedSnippets = (params, callback, noResCB) => dispatch => {
    if (params)
        params = {...params, languages: Snippler.constructLanguagesFilter()};
    else
        params = {languages: Snippler.constructLanguagesFilter()};

    Snippler.snippet().getMostSaved(params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({type: FETCH_MOST_SAVED, payload: res.data});
        }
    });
};


export const setShouldIncreaseView = () => dispatch => {
    dispatch({type: SHOULD_INCREASE_VIEW});
};

export const resetShouldIncreaseView = () => dispatch => {
    dispatch({type: RESET_INCREASE_VIEW});
};
