import React, {Component} from "react";
import PropTypes from "prop-types";
import {connect} from "react-redux";

import InputLabel from "@material-ui/core/InputLabel";
import Paper from "@material-ui/core/Paper";
import Tooltip from "@material-ui/core/Tooltip";
import Button from "@material-ui/core/Button";
import IconButton from "@material-ui/core/IconButton";
import ThumbDownAlt from "@material-ui/icons/ThumbDownAlt";
import ThumbUpAlt from "@material-ui/icons/ThumbUpAlt";
import Bookmark from "@material-ui/icons/Bookmark";
import Visibility from "@material-ui/icons/Visibility";

import history from "../../../root/history";

import {styles} from "./styles";
import {
    DISMISS_MSG,
    DOWNVOTE_SNPT_SIGNIN_MSG,
    LOGIN_PATH,
    SAVE_SNPT_SIGNIN_MSG,
    SIGNIN_MSG,
    snippetDetailsPath,
    UPVOTE_SNPT_SIGNIN_MSG
} from "../../../constants/constants";

import {
    downvoteSnippet,
    saveSnippet,
    setShouldIncreaseView,
    upvoteSnippet
} from "../../../redux/actions/snippetActions";
import {closeBinaryAlert, showBinaryAlert} from "../../../redux/actions/alertActions";


class SnippetItem extends Component {
    onClickSnippet = () => {
        let snippetId = this.props.snippetId;

        this.props.setShouldIncreaseView();
        history.push(snippetDetailsPath(snippetId));
    };


    redirectToLogin = () => {
        history.push(LOGIN_PATH);
        this.props.closeBinaryAlert();
    };


    handleSaveSnippet = (event) => {
        let actionOne = {title: DISMISS_MSG};
        let actionTwo = {title: SIGNIN_MSG, callback: this.redirectToLogin};
        if (!this.props.auth.loggedIn)
            this.props.showBinaryAlert(`${SIGNIN_MSG}?`, SAVE_SNPT_SIGNIN_MSG, actionOne, actionTwo);

        const snippetId = this.props.snippetId;
        const saved = this.props.snippets.byIds[snippetId].saved;

        if (saved !== undefined)
            this.props.saveSnippet(snippetId, !saved);
    };


    handleUpvoteSnippet = (event) => {
        let actionOne = {title: DISMISS_MSG};
        let actionTwo = {title: SIGNIN_MSG, callback: this.redirectToLogin};
        if (!this.props.auth.loggedIn)
            this.props.showBinaryAlert(`${SIGNIN_MSG}?`, UPVOTE_SNPT_SIGNIN_MSG, actionOne, actionTwo);

        const snippetId = this.props.snippetId;
        const upvoted = this.props.snippets.byIds[snippetId].upvoted;
        if (upvoted !== undefined)
            this.props.upvoteSnippet(snippetId, !upvoted);
    };


    handleDownvoteSnippet = (event) => {
        let actionOne = {title: DISMISS_MSG};
        let actionTwo = {title: SIGNIN_MSG, callback: this.redirectToLogin};
        if (!this.props.auth.loggedIn)
            this.props.showBinaryAlert(`${SIGNIN_MSG}?`, DOWNVOTE_SNPT_SIGNIN_MSG, actionOne, actionTwo);

        const snippetId = this.props.snippetId;
        const downvoted = this.props.snippets.byIds[snippetId].downvoted;
        if (downvoted !== undefined)
            this.props.downvoteSnippet(snippetId, !downvoted);
    };


    render() {
        let snippet = this.props.snippets.byIds[this.props.snippetId];

        // Merge style
        let rootCtn = styles.rootCtn;
        if (this.props.style) {
            rootCtn = {...rootCtn, ...this.props.style};
        }

        return (
            <Paper style={rootCtn}>
                <div style={styles.leftCtn}>
                    <InputLabel style={styles.language}>
                        {snippet.languageName}
                    </InputLabel>
                </div>

                <div style={styles.middleCtn}>
                    <div style={styles.titleCtn} onClick={this.onClickSnippet}>
                        <p style={styles.title}>{snippet.title}</p>
                    </div>
                    <div style={styles.iconsCtn}>
                        <Tooltip disableFocusListener disableTouchListener title="Save">
                            <IconButton
                                style={snippet.saved ? {...styles.iconBtn, color: '#DE555C'} : styles.iconBtn}
                                onClick={this.handleSaveSnippet}
                            >
                                <Bookmark style={styles.icon}/>
                                {snippet.savedCount}
                            </IconButton>
                        </Tooltip>
                        <Tooltip disableFocusListener disableTouchListener title="Upvote">
                            <IconButton
                                style={snippet.upvoted ? {...styles.iconBtn, color: '#DE555C'} : styles.iconBtn}
                                onClick={this.handleUpvoteSnippet}
                            >
                                <ThumbUpAlt style={styles.icon}/>
                                {snippet.upvotes}
                            </IconButton>
                        </Tooltip>
                        <Tooltip disableFocusListener disableTouchListener title="Downvote">
                            <IconButton
                                style={snippet.downvoted ? {...styles.iconBtn, color: '#DE555C'} : styles.iconBtn}
                                onClick={this.handleDownvoteSnippet}
                            >
                                <ThumbDownAlt style={styles.icon}/>
                                {snippet.downvotes}
                            </IconButton>
                        </Tooltip>
                    </div>
                </div>


                <div style={styles.rightCtn}>
                    <Button style={styles.viewIconBtn} disabled>
                        <Visibility style={styles.viewIcon}/>
                        <p style={styles.viewText}>
                            {snippet.viewsCount}
                        </p>
                    </Button>
                </div>
            </Paper>
        );
    }
}


SnippetItem.propTypes = {
    snippetId: PropTypes.string.isRequired
};

function mapStateToProps(state) {
    return {
        auth: state.auth,
        snippets: state.snippets
    };
}


export default connect(mapStateToProps, {
    upvoteSnippet,
    downvoteSnippet,
    saveSnippet,
    showBinaryAlert,
    closeBinaryAlert,
    setShouldIncreaseView
})(SnippetItem);