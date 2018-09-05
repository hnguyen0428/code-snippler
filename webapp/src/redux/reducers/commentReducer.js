import {CREATE_COMMENT, FETCH_COMMENTS, UPDATE_COMMENT, DELETE_COMMENT, UPVOTE_COMMENT, DOWNVOTE_COMMENT}
from '../actions/types';


const initialState = {
    byIds: {}
};


export default function(state = initialState, action) {
    switch (action.type) {
        case CREATE_COMMENT:
        case UPDATE_COMMENT: {
            let comment = action.payload;
            let byIds = state.byIds;

            // Merge the two if this comment existed in the state before
            if (byIds[comment.commentId])
                comment = {...byIds[comment.commentId], ...comment};

            return {
                ...state,
                byIds: {
                    ...state.byIds,
                    [comment.commentId]: comment
                }
            };
        }
        case FETCH_COMMENTS: {
            let byIds = state.byIds;
            let comments = action.payload;
            comments.forEach(comment => {
                if (byIds[comment.commentId])
                    byIds[comment.commentId] = {...byIds[comment.commentId], ...comment};
                else
                    byIds[comment.commentId] = comment;
            });
            return {
                ...state,
                byIds: byIds
            };
        }
        case DELETE_COMMENT: {
            let commentId = action.payload;
            // Delete from state the snippet that was deleted
            let byIds = state.byIds;
            if (byIds[commentId])
                delete byIds[commentId];

            return {
                ...state,
                byIds: byIds
            };
        }
        case UPVOTE_COMMENT: {
            // Data contains {commentId: <id>, upvoted: <true/false>}
            let data = action.payload;
            let byIds = state.byIds;
            if (byIds[data.commentId]) {
                if (byIds[data.commentId].upvoted !== data.upvoted) {
                    byIds[data.commentId].upvoted = data.upvoted;
                    byIds[data.commentId].upvotes = data.upvoted ? byIds[data.commentId].upvotes + 1 :
                        byIds[data.commentId].upvotes - 1;

                    // If it was downvoted before, then make it false
                    if (byIds[data.commentId].downvoted && data.upvoted) {
                        byIds[data.commentId].downvoted = false;
                        byIds[data.commentId].downvotes -= 1;
                    }
                }
            }
            return {
                ...state,
                byIds: byIds
            };
        }
        case DOWNVOTE_COMMENT: {
            // Data contains {commentId: <id>, downvoted: <true/false>}
            let data = action.payload;
            let byIds = state.byIds;
            if (byIds[data.commentId]) {
                if (byIds[data.commentId].downvoted !== data.downvoted) {
                    byIds[data.commentId].downvoted = data.downvoted;
                    byIds[data.commentId].downvotes = data.downvoted ? byIds[data.commentId].downvotes + 1 :
                        byIds[data.commentId].downvotes - 1;

                    // If it was upvoted before, then make it false
                    if (byIds[data.commentId].upvoted && data.downvoted) {
                        byIds[data.commentId].upvoted = false;
                        byIds[data.commentId].upvotes -= 1;
                    }
                }
            }
            return {
                ...state,
                byIds: byIds
            };
        }
        default:
            return state;
    }
}