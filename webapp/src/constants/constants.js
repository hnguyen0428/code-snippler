
export const SNIPPETS_SEARCH_BAR_PH = "Search for snippets...";
export const ADD_COMMENT_PH = "Add a comment...";
export const SNIPPET_TITLE_PH = "Title of your Snippet";
export const SNIPPET_DESC_PH = "Describe what your snippet is about...";


// ALERT ACTIONS TITLES
export const DISMISS_MSG = "Dismiss";
export const SIGNIN_MSG = "Login";
export const DELETE_MSG = "Delete";
export const SNIPPET_CREATE_MSG = "Post";

export const CREATE_SNPT_SIGNIN_MSG = "To create a snippet, you must sign in";

export const UPVOTE_CMT_SIGNIN_MSG = "To upvote the comment, you must sign in";
export const DOWNVOTE_CMT_SIGNIN_MSG = "To downvote the comment, you must sign in";
export const DELETE_CMT_VERIFY_MSG = "Are you sure you want to delete this comment?";

export const UPVOTE_SNPT_SIGNIN_MSG = "To upvote the snippet, you must sign in";
export const DOWNVOTE_SNPT_SIGNIN_MSG = "To downvote the snippet, you must sign in";
export const SAVE_SNPT_SIGNIN_MSG = "To save the snippet, you must sign in";
export const DELETE_SNPT_VERIFY_MSG = "Are you sure you want to delete this snippet?";

export const SNIPPET_CREATE_VERIFY_MSG = "Do you want to post this snippet?";



// ROUTE PATHS
export const MY_PROFILE_PATH = "/user/me";
export const userProfilePath = (userId) => (`/user/${userId}`);
export const LOGIN_PATH = "/login";

export const snippetDetailsPath = (snippetId) => (`/snippet/${snippetId}`);
export const editSnippetPath = (snippetId) => (`/snippet?snippetId=${snippetId}`);
export const SNIPPET_CREATE_PATH = '/snippet';
export const SNIPPET_SEARCH_PATH = '/search';


export const PASSWORD_INPUT_LABEL = "Password must be alphanumeric and contains between 6-20 characters";
export const USERNAME_INPUT_LABEL = "Username must contain only the characters [a-z, A-Z, 0-9, _] and be " +
    "between 6-20 characters";
export const REGISTER_BUTTON_LABEL = "Become a Code Snippler";
export const LOGIN_BUTTON_LABEL = "Login";

export const USERNAME_CHAR_INVALID_ERR = "Username must contain only [a-zA-Z0-9_].";
export const USERNAME_LENGTH_ERR = "Username must be between 6 to 20 characters.";
export const PASSWORD_CHAR_INVALID_ERR = "Password must be alphanumeric.";
export const PASSWORD_LENGTH_ERR = "Password must be between 6 to 20 characters.";
export const CONF_PW_NOT_MATCHED = "Confirmation password does not match password";

export const WELCOME_MSG = "Welcome";
export const REDIRECT_MSG = "We will redirect you to the homepage in a bit...";

export const INV_EMAIL_MSG = "Email is not valid";
export const FIRST_NAME_LIMIT = 256;
export const LAST_NAME_LIMIT = 256;
export const EMAIL_LIMIT = 256;

export const THEIR_CREATED_SNIPPETS_TAB = "Snippets They Created";
export const THEIR_SAVED_SNIPPETS_TAB = "Snippets They Saved";
export const YOUR_CREATED_SNIPPETS_TAB = "Snippets You Created";
export const YOUR_SAVED_SNIPPETS_TAB = "Snippets You Saved";

export const SNIPPET_TITLE_LIMIT = 256;
export const SNIPPET_DESC_LIMIT = 3000;
export const SNIPPET_CODE_LIMIT = 8000;
export const SNIPPET_TITLE_EMPTY_ERR = "Title must not be empty";
export const SNIPPET_LANG_CHOOSE_ERR = "Language/Technology must be chosen";
export const SNIPPET_EMPTY_CODE_ERR = "Cannot submit empty code";
export const SNIPPET_CODE_LENGTH_ERR = `Code contains more characters than ${SNIPPET_CODE_LIMIT}`;
export const SNIPPET_UPDATE_BTN = "Update";
export const SNIPPET_CREATE_BTN = "Post";


// MISC
export const PASSWORD_RANGE = {
    low: 6,
    high: 20
};

export const USERNAME_RANGE = {
    low: 6,
    high: 20
};