import React, { Component } from 'react';
import {connect} from 'react-redux';
import {withRouter} from 'react-router-dom';
import {withStyles} from '@material-ui/core';
import history from '../../../root/history';

import AceEditor from 'react-ace';

import CommentsList from '../../dumb/CommentsList/CommentsList';

import {styles, materialStyles} from './styles';

import {
    fetchSnippet,
    upvoteSnippet,
    downvoteSnippet,
    saveSnippet,
    resetShouldIncreaseView,
    fetchComments,
    createComment,
    deleteSnippet
} from "../../../redux/actions/snippetActions";
import {overridePath, resetOverridePath} from "../../../redux/actions/routerActions";
import {showBinaryAlert, closeBinaryAlert} from '../../../redux/actions/alertActions';

import Edit from '@material-ui/icons/Edit';
import Delete from '@material-ui/icons/Delete';
import IconButton from '@material-ui/core/IconButton';
import Button from '@material-ui/core/Button';
import InputLabel from '@material-ui/core/InputLabel';
import TextField from '@material-ui/core/TextField';
import Avatar from '@material-ui/core/Avatar';
import Tooltip from '@material-ui/core/Tooltip';
import Popper from '@material-ui/core/Popper';
import Paper from '@material-ui/core/Paper';
import Grow from '@material-ui/core/Grow';
import ClickAwayListener from '@material-ui/core/ClickAwayListener';
import MenuItem from '@material-ui/core/MenuItem';
import MenuList from '@material-ui/core/MenuList';

import ThumbDownAlt from '@material-ui/icons/ThumbDownAlt';
import ThumbUpAlt from '@material-ui/icons/ThumbUpAlt';
import Bookmark from '@material-ui/icons/Bookmark';
import Settings from '@material-ui/icons/Settings';

import {languagesMap} from '../../../constants/languages';

import 'brace/theme/github';
import 'brace/theme/xcode';
import 'brace/theme/tomorrow';


import {readAceConfig as aceConfig, editorTheme} from '../../../constants/AceConfig';
import SnipplerConfig from '../../../constants/SnipplerConfig'

for (let language in languagesMap) {
    let mode = languagesMap[language];
    require(`brace/mode/${mode}`);
}

const moment = require('moment');


class SnippetDetailsPage extends Component {
    constructor(props) {
        super(props);

        this.queryingComments = false;

        let sortMode = localStorage.getItem('commentsSortMode');

        // Use this sort mode in order to query comments right away so we don't have
        // to wait for the state to propagate through
        this.sortMode = sortMode ? sortMode : 'mostRecent';

        this.state = {
            params: {},
            commentText: '',
            currCommentsPage: 0,
            comments: {},
            sortMode: sortMode ? sortMode : 'mostRecent',
            commentsListSettingsOpen: false,
            doneFetchingComments: false
        }
    }

    componentWillMount() {
        let params = {
            showUserDetails: true
        };

        if (this.props.snippets.increaseViewcount)
            params = {...params, increaseViewcount: true};

        this.props.fetchSnippet(this.props.match.params.snippetId, params);
        this.props.resetShouldIncreaseView();

        this.queryComments();
    }


    queryComments = (queryNext) => {
        if (!this.queryingComments) {
            this.setState({doneFetchingComments: false});
            this.queryingComments = true;
            const nextPage = this.state.currCommentsPage + 1;
            const queryPage = queryNext !== undefined && queryNext ? nextPage : this.state.currCommentsPage;
            const params = {
                showUserDetails: true,
                page: queryPage,
                pageSize: SnipplerConfig.COMMENTS_LIST_SIZE,
                sort: this.sortMode
            };

            this.props.fetchComments(this.props.match.params.snippetId, params,
                (res, err) => {
                    this.queryingComments = false;
                    if (res) {
                        let comments = res.data;
                        let commentIds = [];
                        comments.forEach(comment => {commentIds.push(comment.commentId)});

                        if (queryNext)
                            this.setState({
                                currCommentsPage: nextPage,
                                comments: {
                                    ...this.state.comments,
                                    [queryPage]: commentIds
                                },
                                doneFetchingComments: true
                            });
                        else
                            this.setState({
                                comments: {
                                    ...this.state.comments,
                                    [queryPage]: commentIds
                                },
                                doneFetchingComments: true
                            });
                    }
                });
        }
    };


    onClickEdit = () => {
        this.props.overridePath(this.props.location.pathname);
        history.push('/snippet?snippetId=' + this.props.match.params.snippetId);
    };

    onClickDelete = () => {
        let actionOne = {title: 'Dismiss'};
        let actionTwo = {title: 'Delete', callback: this.deleteSnippet};
        this.props.showBinaryAlert('Delete?', 'Are you sure you want to delete this snippet?', actionOne, actionTwo);
    };

    deleteSnippet = () => {
        let snippetId = this.props.match.params.snippetId;
        this.props.deleteSnippet(snippetId, (res, err) => {
            this.props.closeBinaryAlert();
            if (res) {
                let prevPath = this.props.router.prevPath;
                let overridePath = this.props.router.overridePath;
                if (overridePath)
                    history.push(overridePath);
                else
                    history.push(prevPath);

                this.props.resetOverridePath();
            }
        });
    };


    onClickUsername = () => {
        let snippetId = this.props.match.params.snippetId;
        let snippet = this.props.snippets.byIds[snippetId];

        if (this.props.auth.loggedIn && snippet.userId === this.props.auth.currentUser.userId)
            history.push('/user/me');
        else
            history.push('/user/' + snippet.userId);
    };

    onChangeComment = (event) => {
        this.setState({commentText: event.target.value});
    };

    onClickComment = () => {
        this.props.createComment(this.props.match.params.snippetId, {
            content: this.state.commentText
        }, (res, err) => {
            if (res) {
                this.setState({commentText: ''});
                this.queryComments();
            }
        });
    };

    redirectToLogin = () => {
        history.push('/login');
        this.props.closeBinaryAlert();
    };


    handleSaveSnippet = (event) => {
        let actionOne = {title: 'Dismiss'};
        let actionTwo = {title: 'Sign In', callback: this.redirectToLogin};
        if (!this.props.auth.loggedIn)
            this.props.showBinaryAlert('Sign In?', 'To save the snippet, you must sign in', actionOne, actionTwo);

        const snippetId = this.props.match.params.snippetId;
        const saved = this.props.snippets.byIds[snippetId].saved;

        if (saved !== undefined)
            this.props.saveSnippet(snippetId, !saved);
    };


    handleUpvoteSnippet = (event) => {
        let actionOne = {title: 'Dismiss'};
        let actionTwo = {title: 'Sign In', callback: this.redirectToLogin};
        if (!this.props.auth.loggedIn)
            this.props.showBinaryAlert('Sign In?', 'To upvote the snippet, you must sign in', actionOne, actionTwo);

        const snippetId = this.props.match.params.snippetId;
        const upvoted = this.props.snippets.byIds[snippetId].upvoted;
        if (upvoted !== undefined)
            this.props.upvoteSnippet(snippetId, !upvoted);
    };


    handleDownvoteSnippet = (event) => {
        let actionOne = {title: 'Dismiss'};
        let actionTwo = {title: 'Sign In', callback: this.redirectToLogin};
        if (!this.props.auth.loggedIn)
            this.props.showBinaryAlert('Sign In?', 'To downvote the snippet, you must sign in', actionOne, actionTwo);

        const snippetId = this.props.match.params.snippetId;
        const downvoted = this.props.snippets.byIds[snippetId].downvoted;
        if (downvoted !== undefined)
            this.props.downvoteSnippet(snippetId, !downvoted);
    };

    commentsScrolledToBottom = (commentsList) => {
        this.queryComments(true);
    };

    toggleCommentsListSettings = () => {
        this.setState({commentsListSettingsOpen: !this.state.commentsListSettingsOpen});
    };

    closeCommentsListSettings = (event) => {
        if (this.settingsButton.contains(event.target)) {
            return;
        }
        this.setState({commentsListSettingsOpen: false});
    };

    handleCommentsSettingsChange = (event) => {
        this.setState({
            sortMode: event.target.id
        });
        this.sortMode = event.target.id;
        localStorage.setItem('commentsSortMode', event.target.id);
        this.queryComments();
    };


    render() {
        let snippetId = this.props.match.params.snippetId;
        let snippet = this.props.snippets.byIds[snippetId];
        let currentUser = this.props.auth.currentUser;

        const {classes} = this.props;


        if (snippet) {
            let user = this.props.users.byIds[snippet.userId];
            let username = user ? user.username : '';

            let date = new Date(snippet.createdDate);
            date = moment(date).calendar();

            let updatedDate = null;
            if (snippet.updatedDate) {
                updatedDate = new Date(snippet.updatedDate);
                updatedDate = moment(updatedDate).calendar();
            }

            let comments = [];
            let pages = Object.keys(this.state.comments).sort();
            for (let page of pages) {
                this.state.comments[page].forEach(id => {
                    if (this.props.comments.byIds[id])
                        comments.push(this.props.comments.byIds[id]);
                })
            }

            return (
                <div style={styles.rootCtn}>
                    <div style={styles.contentCtn}>
                        <div style={styles.metadataCtn}>
                            <div style={styles.actionsCtn}>
                                <div style={styles.usernameCtn}>
                                    <IconButton style={styles.profileIcon} onClick={this.onClickUsername}>
                                        <Avatar>
                                            {username.substr(0, 1).toUpperCase()}
                                        </Avatar>
                                    </IconButton>
                                    <InputLabel onClick={this.onClickUsername}
                                                style={styles.username}>
                                        {username}
                                    </InputLabel>
                                </div>

                                {/* Space in between */}
                                <div style={{flex: '1'}}/>

                                { this.props.auth.loggedIn && snippet.userId === this.props.auth.currentUser.userId &&
                                    <div style={styles.editButton}>
                                        <IconButton onClick={this.onClickEdit}>
                                            <Edit/>
                                        </IconButton>
                                        <IconButton onClick={this.onClickDelete}>
                                            <Delete/>
                                        </IconButton>
                                    </div>
                                }
                            </div>

                            <br/>

                            <InputLabel style={styles.header}>{snippet.title}</InputLabel>


                            <div style={styles.descriptionCtn}>
                                {snippet.description &&
                                    <TextField
                                        multiline
                                        value={snippet.description}
                                        style={styles.description}
                                        fullWidth
                                        disabled
                                        InputProps={{disableUnderline: true, classes: {input: classes.label}}}
                                    />
                                }
                            </div>

                            <div style={styles.dateCtn}>
                                { date &&
                                <InputLabel style={styles.dateLabel}>
                                    Created {date}
                                </InputLabel>
                                }
                                { updatedDate &&
                                <InputLabel style={styles.dateLabel}>
                                    Updated {updatedDate}
                                </InputLabel>
                                }
                            </div>

                        </div>
                        <AceEditor
                            id="ace-editor"
                            mode={languagesMap[snippet.languageName.toLowerCase()]}
                            theme={editorTheme}
                            readOnly
                            style={styles.editor}
                            value={snippet.code}
                            showPrintMargin={false}
                            tabSize={4}
                            wrapEnabled
                            setOptions={aceConfig}
                        />

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

                        {
                            currentUser &&
                            <TextField
                                style={styles.commentTextField}
                                placeholder="Add a comment..." fullWidth
                                helperText={
                                    <Button color="primary"
                                            disabled={this.state.commentText.length === 0}
                                            onClick={this.onClickComment}
                                    >
                                        Comment
                                    </Button>
                                }
                                FormHelperTextProps={{style: styles.commentBoxBtn}}
                                InputProps={
                                    {
                                        startAdornment: <Avatar style={styles.commentBoxAvatar}>
                                            {currentUser.username.substr(0, 1).toUpperCase()}
                                        </Avatar>,
                                        onChange: this.onChangeComment
                                    }
                                }
                                value={this.state.commentText}
                            />
                        }

                        <div style={styles.commentsListCtn}>
                            <IconButton buttonRef={node => {this.settingsButton = node;}}
                                        onClick={this.toggleCommentsListSettings}
                                        style={styles.commentsSettingsIcon}
                            >
                                <Settings/>
                            </IconButton>
                            <CommentsList
                                style={styles.commentsList} comments={comments}
                                onScrollToBottom={this.commentsScrolledToBottom}
                                loading={!this.state.doneFetchingComments}
                            />
                        </div>
                    </div>

                    <Popper open={this.state.commentsListSettingsOpen} anchorEl={this.settingsButton}
                            transition disablePortal>
                        {({TransitionProps, placement}) => (
                            <Grow
                                {...TransitionProps}
                                style={{transformOrigin: placement === 'bottom' ? 'center top' : 'center bottom'}}
                            >
                                <Paper>
                                    <ClickAwayListener onClickAway={this.closeCommentsListSettings}>
                                        <MenuList>
                                            <MenuItem disabled>Filter By</MenuItem>
                                            <MenuItem id="mostRecent"
                                                      onClick={this.handleCommentsSettingsChange}
                                                      selected={this.state.sortMode === 'mostRecent'}
                                            >
                                                Most Recent
                                            </MenuItem>
                                            <MenuItem id="mostUpvoted"
                                                      onClick={this.handleCommentsSettingsChange}
                                                      selected={this.state.sortMode === 'mostUpvoted'}
                                            >
                                                Most Upvoted
                                            </MenuItem>
                                        </MenuList>
                                    </ClickAwayListener>
                                </Paper>
                            </Grow>
                        )}
                    </Popper>
                </div>
            );
        }
        return <div/>;
    }
}


function mapStateToProps(state) {
    return {
        auth: state.auth,
        snippets: state.snippets,
        users: state.users,
        comments: state.comments,
        router: state.router
    };
}


export default withRouter(connect(mapStateToProps,
    {
        fetchSnippet,
        upvoteSnippet,
        downvoteSnippet,
        saveSnippet,
        resetShouldIncreaseView,
        overridePath,
        resetOverridePath,
        fetchComments,
        createComment,
        deleteSnippet,
        showBinaryAlert,
        closeBinaryAlert
    }
)(withStyles(materialStyles)(SnippetDetailsPage)));