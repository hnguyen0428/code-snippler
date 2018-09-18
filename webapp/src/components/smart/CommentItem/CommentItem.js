import React, {Component} from "react";
import {withStyles} from "@material-ui/core";
import {connect} from "react-redux";
import PropTypes from "prop-types";

import Avatar from "@material-ui/core/Avatar";
import ListItem from "@material-ui/core/ListItem";
import TextField from "@material-ui/core/TextField";
import InputLabel from "@material-ui/core/InputLabel";
import Tooltip from "@material-ui/core/Tooltip";
import IconButton from "@material-ui/core/IconButton";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";

import MoreVert from "@material-ui/icons/MoreVert";
import Done from "@material-ui/icons/Done";
import ThumbDownAlt from "@material-ui/icons/ThumbDownAlt";
import ThumbUpAlt from "@material-ui/icons/ThumbUpAlt";

import history from "../../../root/history";

import {deleteComment, downvoteComment, updateComment, upvoteComment} from "../../../redux/actions/commentActions";
import {closeBinaryAlert, showBinaryAlert} from "../../../redux/actions/alertActions";

import {materialStyles, styles} from "./styles";
import {
    DELETE_CMT_VERIFY_MSG,
    DELETE_MSG,
    DISMISS_MSG,
    DOWNVOTE_CMT_SIGNIN_MSG,
    LOGIN_PATH,
    MY_PROFILE_PATH,
    SIGNIN_MSG,
    UPVOTE_CMT_SIGNIN_MSG,
    userProfilePath
} from "../../../constants/constants";

const moment = require('moment');


class CommentItem extends Component {
    constructor(props) {
        super(props);

        this.state = {
            editMode: false,
            commentText: this.props.comment.content,
            anchorEl: null,
        };
    }


    redirectToLogin = () => {
        history.push(LOGIN_PATH);
        this.props.closeBinaryAlert();
    };


    handleUpvoteComment = () => {
        let actionOne = {title: DISMISS_MSG};
        let actionTwo = {title: SIGNIN_MSG, callback: this.redirectToLogin};
        if (!this.props.auth.loggedIn)
            this.props.showBinaryAlert(`${SIGNIN_MSG}?`, UPVOTE_CMT_SIGNIN_MSG, actionOne, actionTwo);

        const commentId = this.props.comment.commentId;
        const upvoted = this.props.comments.byIds[commentId].upvoted;
        if (upvoted !== undefined)
            this.props.upvoteComment(commentId, !upvoted);
    };

    handleDownvoteComment = () => {
        let actionOne = {title: DISMISS_MSG};
        let actionTwo = {title: SIGNIN_MSG, callback: this.redirectToLogin};
        if (!this.props.auth.loggedIn)
            this.props.showBinaryAlert(`${SIGNIN_MSG}?`, DOWNVOTE_CMT_SIGNIN_MSG, actionOne, actionTwo);

        const commentId = this.props.comment.commentId;
        const downvoted = this.props.comments.byIds[commentId].downvoted;
        if (downvoted !== undefined)
            this.props.downvoteComment(commentId, !downvoted);
    };


    onEditComment = (event) => {
        if (this.state.editMode)
            this.setState({commentText: event.target.value});
    };


    onClickUsername = () => {
        if (this.props.auth.currentUser && this.props.auth.currentUser.userId === this.props.comment.userId)
            history.push(MY_PROFILE_PATH);
        else
            history.push(userProfilePath(this.props.comment.userId));
    };

    onClickEdit = () => {
        this.setState({
            editMode: true,
            anchorEl: null
        });
    };

    deleteComment = () => {
        this.props.deleteComment(this.props.comment.commentId, (res, err) => {
            this.props.closeBinaryAlert();
        });
    };

    onClickDelete = () => {
        let actionOne = {title: DISMISS_MSG};
        let actionTwo = {title: DELETE_MSG, callback: this.deleteComment};
        this.props.showBinaryAlert(`${DELETE_MSG}?`, DELETE_CMT_VERIFY_MSG, actionOne, actionTwo);
        this.setState({
            anchorEl: null
        });
    };

    closeMoreOptionsMenu = () => {
        this.setState({
            anchorEl: null
        });
    };

    doneEditing = () => {
        this.props.updateComment(this.props.comment.commentId, {
            content: this.state.commentText
        }, (res, err) => {
            if (res)
                this.setState({editMode: false});
        })
    };

    handleMoreOptions = (event) => {
        this.setState({
            anchorEl: event.currentTarget
        });
    };


    render() {
        let {classes, style, comment, ...props} = this.props;

        style = {...styles.rootCtn, ...style, };

        let formattedDate = moment(comment.createdDate).calendar();
        if (comment.updatedDate)
            formattedDate = 'Updated ' + moment(comment.updatedDate).calendar();

        return (
            <ListItem style={style} {...props}>
                <div style={styles.avatarCtn}>
                    {
                        comment.user &&
                            <IconButton onClick={this.onClickUsername}>
                                <Avatar>
                                    {comment.user.username.substr(0, 1).toUpperCase()}
                                </Avatar>
                            </IconButton>
                    }
                </div>

                <div style={styles.contentCtn}>
                    {
                        comment.user &&
                            <InputLabel style={styles.username} onClick={this.onClickUsername}>
                                {comment.user.username}
                            </InputLabel>
                    }

                    {
                        comment.content &&
                            <div style={styles.textfieldCtn}>
                                <TextField
                                    multiline
                                    value={this.state.editMode ? this.state.commentText : comment.content}
                                    style={styles.commentContent}
                                    fullWidth
                                    disabled={!this.state.editMode}
                                    onChange={this.onEditComment}
                                    label={formattedDate}
                                    InputProps={{disableUnderline: !this.state.editMode, classes: {input: classes.commentContent}}}
                                />

                                { this.props.auth.loggedIn && this.props.auth.currentUser.userId === comment.userId &&
                                    <IconButton buttonRef={node => {this.anchorEl = node;}}
                                                onClick={this.state.editMode ? this.doneEditing : this.handleMoreOptions}
                                                style={styles.editBtn}
                                                disableRipple
                                    >
                                        {
                                            this.state.editMode ?
                                                <Done/>
                                                :
                                                <MoreVert/>
                                        }
                                    </IconButton>
                                }
                            </div>
                    }
                    <div style={styles.toolbar}>
                        <Tooltip disableFocusListener disableTouchListener title="Upvote">
                            <IconButton
                                style={comment.upvoted ? {...styles.iconBtn, color: '#DE555C'} : styles.iconBtn}
                                onClick={this.handleUpvoteComment}
                            >
                                <ThumbUpAlt style={styles.icon}/>
                                {comment.upvotes}
                            </IconButton>
                        </Tooltip>
                        <Tooltip disableFocusListener disableTouchListener title="Downvote">
                            <IconButton
                                style={comment.downvoted ? {...styles.iconBtn, color: '#DE555C'} : styles.iconBtn}
                                onClick={this.handleDownvoteComment}
                            >
                                <ThumbDownAlt style={styles.icon}/>
                                {comment.downvotes}
                            </IconButton>
                        </Tooltip>
                    </div>
                </div>

                <Menu
                    anchorEl={this.state.anchorEl}
                    open={Boolean(this.state.anchorEl)}
                    onClose={this.closeMoreOptionsMenu}
                >
                    <MenuItem id={this.MOST_POPULAR_MENUITEM}
                              onClick={this.onClickEdit}
                    >
                        Edit
                    </MenuItem>
                    <MenuItem id={this.MOST_VIEWS_MENUITEM}
                              onClick={this.onClickDelete}
                    >
                        Delete
                    </MenuItem>
                </Menu>
            </ListItem>
        );
    }
}


function mapStateToProps(state) {
    return {
        auth: state.auth,
        comments: state.comments
    };
}


CommentItem.propTypes = {
    comment: PropTypes.object.isRequired
};


export default connect(mapStateToProps, {
    updateComment,
    upvoteComment,
    downvoteComment,
    deleteComment,
    showBinaryAlert,
    closeBinaryAlert
})(withStyles(materialStyles)(CommentItem));