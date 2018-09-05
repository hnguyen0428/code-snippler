import {CREATE_SNIPPET, FETCH_SNIPPET, UPDATE_SNIPPET, DELETE_SNIPPET, UPVOTE_SNIPPET, DOWNVOTE_SNIPPET,
    SAVE_SNIPPET, FETCH_POPULAR, FETCH_MOST_VIEWS, FETCH_MOST_SAVED, FETCH_MOST_UPVOTES,
    FETCH_MY_SAVED_SNIPPETS, FETCH_MY_CREATED_SNIPPETS, FETCH_USER_SAVED_SNIPPETS,
    FETCH_USER_CREATED_SNIPPETS, FETCH_SNIPPETS, SHOULD_INCREASE_VIEW, RESET_INCREASE_VIEW} from '../actions/types';


const initialState = {
    byIds: {},
    increaseViewcount: false
};


export default function(state = initialState, action) {
    switch (action.type) {
        case CREATE_SNIPPET:
        case FETCH_SNIPPET:
        case UPDATE_SNIPPET: {
            let snippet = action.payload;
            // If this snippet was in the state before, then merge the two objects with
            // the payload as the priority
            if (state.byIds[snippet.snippetId])
                snippet = {...state.byIds[snippet.snippetId], ...snippet};

            return {
                ...state,
                byIds: {
                    ...state.byIds,
                    [snippet.snippetId]: snippet
                }
            };
        }
        case DELETE_SNIPPET: {
            let snippetId = action.payload;
            // Delete from state the snippet that was deleted
            let byIds = state.byIds;
            if (byIds[snippetId])
                delete byIds[snippetId];

            return {
                ...state,
                byIds: byIds
            };
        }
        case UPVOTE_SNIPPET: {
            // Data contains {snippetId: <id>, upvoted: <true/false>}
            let data = action.payload;
            let byIds = state.byIds;
            if (byIds[data.snippetId]) {
                if (byIds[data.snippetId].upvoted !== data.upvoted) {
                    byIds[data.snippetId].upvoted = data.upvoted;
                    byIds[data.snippetId].upvotes = data.upvoted ? byIds[data.snippetId].upvotes + 1 :
                        byIds[data.snippetId].upvotes - 1;

                    // If it was downvoted before, then make it false
                    if (byIds[data.snippetId].downvoted && data.upvoted) {
                        byIds[data.snippetId].downvoted = false;
                        byIds[data.snippetId].downvotes -= 1;
                    }
                }
            }
            return {
                ...state,
                byIds: byIds
            };
        }
        case DOWNVOTE_SNIPPET: {
            // Data contains {snippetId: <id>, downvoted: <true/false>}
            let data = action.payload;
            let byIds = state.byIds;
            if (byIds[data.snippetId]) {
                if (byIds[data.snippetId].downvoted !== data.downvoted) {
                    byIds[data.snippetId].downvoted = data.downvoted;
                    byIds[data.snippetId].downvotes = data.downvoted ? byIds[data.snippetId].downvotes + 1 :
                        byIds[data.snippetId].downvotes - 1;

                    // If it was upvoted before, then make it false
                    if (byIds[data.snippetId].upvoted && data.downvoted) {
                        byIds[data.snippetId].upvoted = false;
                        byIds[data.snippetId].upvotes -= 1;
                    }
                }
            }
            return {
                ...state,
                byIds: byIds
            };
        }
        case SAVE_SNIPPET: {
            // Data contains {snippetId: <id>, saved: <true/false>}
            let data = action.payload;
            let byIds = state.byIds;
            if (byIds[data.snippetId]) {
                if (byIds[data.snippetId].saved !== data.saved) {
                    byIds[data.snippetId].saved = data.saved;
                    byIds[data.snippetId].savedCount = data.saved ? byIds[data.snippetId].savedCount + 1 :
                        byIds[data.snippetId].savedCount - 1;
                }
            }
            return {
                ...state,
                byIds: byIds
            };
        }
        case FETCH_POPULAR:
        case FETCH_MOST_UPVOTES:
        case FETCH_MOST_VIEWS:
        case FETCH_MOST_SAVED:
        case FETCH_USER_SAVED_SNIPPETS:
        case FETCH_USER_CREATED_SNIPPETS:
        case FETCH_MY_SAVED_SNIPPETS:
        case FETCH_MY_CREATED_SNIPPETS:
        case FETCH_SNIPPETS: {
            let byIds = state.byIds;
            let snippets = action.payload;
            snippets.forEach(snippet => {
                if (byIds[snippet.snippetId])
                    byIds[snippet.snippetId] = {...byIds[snippet.snippetId], ...snippet};
                else
                    byIds[snippet.snippetId] = snippet;
            });
            return {
                ...state,
                byIds: byIds
            };
        }
        case SHOULD_INCREASE_VIEW:
            return {
                ...state,
                increaseViewcount: true
            };
        case RESET_INCREASE_VIEW:
            return {
                ...state,
                increaseViewcount: false
            };
        default:
            return state;
    }
}