import Snippler from '../../api/SnipplerClient';
import {UPDATE_COMMENT, DELETE_COMMENT, UPVOTE_COMMENT, DOWNVOTE_COMMENT, FETCH_COMMENTS, FETCH_USERS} from './types';
import {handleActionsResult} from './actions';


export const fetchCommentsByIds = (commentIds, params, callback, noResCB) => dispatch => {
    Snippler.comment().getComments(commentIds, params, (res, error) => {
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

                dispatch({
                    type: FETCH_USERS,
                    payload: users
                });
            }
        }
    })
};


export const updateComment = (commentId, params, callback, noResCB) => dispatch => {
    Snippler.comment().updateComment(commentId, params, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: UPDATE_COMMENT,
                payload: res.data
            });
        }
    });
};


export const deleteComment = (commentId, callback, noResCB) => dispatch => {
    Snippler.comment().deleteComment(commentId, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: DELETE_COMMENT,
                payload: commentId
            });
        }
    });
};


export const upvoteComment = (commentId, upvote, callback, noResCB) => dispatch => {
    Snippler.comment().upvoteComment(commentId, upvote, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: UPVOTE_COMMENT,
                payload: {commentId: commentId, upvoted: upvote}
            });
        }
    });
};


export const downvoteComment = (commentId, downvote, callback, noResCB) => dispatch => {
    Snippler.comment().downvoteComment(commentId, downvote, (res, error) => {
        let success = handleActionsResult(res, error, callback, noResCB);
        if (success) {
            dispatch({
                type: DOWNVOTE_COMMENT,
                payload: {commentId: commentId, downvoted: downvote}
            });
        }
    });
};