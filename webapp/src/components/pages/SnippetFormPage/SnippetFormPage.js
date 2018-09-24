import React, { Component } from 'react';
import {connect} from 'react-redux';
import {withRouter} from 'react-router-dom';
import {withStyles} from '@material-ui/core';

import TextField from '@material-ui/core/TextField';
import FormHelperText from '@material-ui/core/FormHelperText';
import Button from '@material-ui/core/Button';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import CircularProgress from '@material-ui/core/CircularProgress';
import AceEditor from 'react-ace';

import {styles, materialStyles} from './styles';
import history from '../../../root/history';
import {
    SNIPPET_TITLE_PH, SNIPPET_DESC_PH, SNIPPET_CREATE_MSG, SNIPPET_CREATE_VERIFY_MSG, SNIPPET_TITLE_LIMIT,
    SNIPPET_DESC_LIMIT, SNIPPET_CODE_LIMIT, SNIPPET_TITLE_EMPTY_ERR, SNIPPET_LANG_CHOOSE_ERR,
    SNIPPET_EMPTY_CODE_ERR, SNIPPET_CODE_LENGTH_ERR, SNIPPET_UPDATE_BTN, SNIPPET_CREATE_BTN
} from '../../../constants/constants';

import {supportedLanguages, languagesMap} from '../../../constants/languages';
import {writeAceConfig as aceConfig, editorTheme, supportedThemes} from '../../../constants/AceConfig';
import {createSnippet, updateSnippet, fetchSnippet} from '../../../redux/actions/snippetActions';
import {showAlert, showBinaryAlert, closeBinaryAlert} from '../../../redux/actions/alertActions';
import {resetOverridePath} from '../../../redux/actions/routerActions';

for (let language in languagesMap) {
    let mode = languagesMap[language];
    require(`brace/mode/${mode}`);
}

supportedThemes.forEach(theme => {
    require(`brace/theme/${theme.toLowerCase()}`);
});


class SnippetFormPage extends Component {
    constructor(props) {
        super(props);

        const params = new URLSearchParams(this.props.location.search);
        let snippetId = params.get('snippetId');
        this.posting = false;

        this.state = {
            language: {
                value: '',
                error: false,
                errorMsg: ''
            },
            mode: '',
            code: '',
            title: {
                value: '',
                error: false,
                errorMsg: ''
            },
            description: {
                value: '',
                error: false,
                errorMsg: '',
            },
            updating: snippetId !== null,
            snippetId: snippetId,
            loading: false
        };
    }


    componentWillMount() {
        const params = new URLSearchParams(this.props.location.search);
        let snippetId = params.get('snippetId');

        if (snippetId !== null) {
            let snippet = this.props.snippets.byIds[snippetId];
            if (snippet) {
                if (this.props.auth.currentUser.userId !== snippet.userId)
                    history.push('/');
                else
                    this.setState({
                        title: {...this.state.title, value: snippet.title},
                        description: {...this.state.description, value: snippet.description},
                        language: {...this.state.language, value: snippet.languageName},
                        code: snippet.code,
                        mode: languagesMap[snippet.languageName.toLowerCase()]
                    });
            }
            else {
                this.props.fetchSnippet(snippetId, {showUserDetails: true}, (res, err) => {
                    let snippet = res.data;
                    if (this.props.auth.currentUser.userId !== snippet.userId)
                        history.push('/');
                    else
                        this.setState({
                            title: {...this.state.title, value: snippet.title},
                            description: {...this.state.description, value: snippet.description},
                            language: {...this.state.language, value: snippet.languageName},
                            code: snippet.code,
                            mode: languagesMap[snippet.languageName.toLowerCase()]
                        });
                });
            }
        }
    }


    onEditorValidate = (object) => {

    };


    handleLanguageChange = (event) => {
        this.setState({
            language: {...this.state.language, value: event.target.value},
            mode: languagesMap[event.target.value.toLowerCase()]
        });
    };


    onCodeEditing = (value) => {
        this.setState({
            code: value
        });
    };


    onEditingForm = (event) => {
        switch (event.target.name) {
            case "title":
                if (event.target.value.length > SNIPPET_TITLE_LIMIT)
                    return;
                break;
            case "description":
                if (event.target.value.length > SNIPPET_DESC_LIMIT)
                    return;
                break;
            default:
                break;
        }

        this.setState({
            [event.target.name]: {...this.state[event.target.name], value: event.target.value}
        });
    };


    onClickSubmit = () => {
        let passed = this.sanityCheck();
        if (!passed)
            return;

        if (this.posting)
            return;

        const params = {
            title: this.state.title.value,
            description: this.state.description.value,
            language: this.state.language.value,
            code: this.state.code
        };

        const resCallback = (res, err) => {
            this.setState({loading: false});
            this.posting = false;
            if (res) {
                let prevPath = this.props.router.prevPath;
                let overridePath = this.props.router.overridePath;
                if (overridePath)
                    history.push(overridePath);
                else
                    history.push(prevPath);

                this.props.resetOverridePath();
                this.props.closeBinaryAlert();
            }
        };

        const createSnippetAction = () => {
            if (!this.posting) {
                this.setState({loading: true});
                this.posting = true;
                this.props.createSnippet(params, resCallback);
            }
        };

        const updateSnippetAction = () => {
            if (!this.posting) {
                this.setState({loading: true});
                this.posting = true;
                this.props.updateSnippet(this.state.snippetId, params, resCallback);
            }
        };

        const action = {callback: createSnippetAction};
        const title = SNIPPET_CREATE_MSG;
        const message = SNIPPET_CREATE_VERIFY_MSG;

        if (this.state.updating)
            updateSnippetAction();
        else
            this.props.showBinaryAlert(title, message, null, action);
    };


    sanityCheck = () => {
        let passed = true;
        let errors = {
            title: {...this.state.title},
            description: {...this.state.description},
            language: {...this.state.language}
        };

        if (this.state.title.value.length === 0) {
            passed = false;
            errors.title.error = true;
            errors.title.errorMsg = SNIPPET_TITLE_EMPTY_ERR;
        }

        if (this.state.language.value.length === 0) {
            passed = false;
            errors.language.error = true;
            errors.language.errorMsg = SNIPPET_LANG_CHOOSE_ERR;
        }

        if (this.state.code.length === 0) {
            // If passed before then show the alert, but if there are other errors, don't show
            // the alert
            if (passed) {
                this.props.showAlert('Error', SNIPPET_EMPTY_CODE_ERR);
            }
            passed = false;
        }

        if (this.state.code.length > SNIPPET_CODE_LIMIT) {
            if (passed) {
                this.props.showAlert('Error', SNIPPET_CODE_LENGTH_ERR);
            }
            passed = false;
        }

        if (!passed)
            this.setState(errors);
        else
            this.setState({
                language: {
                    ...this.state.language,
                    error: false,
                    errorMsg: ''
                },
                title: {
                    ...this.state.title,
                    error: false,
                    errorMsg: ''
                },
                description: {
                    ...this.state.description,
                    error: false,
                    errorMsg: ''
                }
            });

        return passed;
    };


    render() {
        const {classes} = this.props;

        return (
            <div style={styles.rootCtn}>
                <div style={styles.contentCtn}>
                    <FormControl error={this.state.title.error} style={styles.textField}>
                        <TextField
                            name="title"
                            label="Title"
                            multiline
                            placeholder={SNIPPET_TITLE_PH}
                            fullWidth
                            value={this.state.title.value}
                            error={this.state.title.error}
                            onChange={this.onEditingForm}
                        />
                        {this.state.title.error
                        && <FormHelperText>{this.state.title.errorMsg}</FormHelperText>}
                        <FormHelperText error={this.state.title.value.length > SNIPPET_TITLE_LIMIT}>
                            {this.state.title.value.length}/{SNIPPET_TITLE_LIMIT}
                        </FormHelperText>
                    </FormControl>

                    <FormControl error={this.state.description.error} style={styles.textField}>
                        <TextField
                            name="description"
                            label="Description"
                            multiline
                            placeholder={SNIPPET_DESC_PH}
                            fullWidth
                            value={this.state.description.value}
                            error={this.state.description.error}
                            onChange={this.onEditingForm}
                        />
                        {this.state.description.error &&
                        <FormHelperText>{this.state.description.errorMsg}</FormHelperText>}
                        <FormHelperText error={this.state.description.value.length > SNIPPET_DESC_LIMIT}>
                            {this.state.description.value.length}/{SNIPPET_DESC_LIMIT}
                        </FormHelperText>
                    </FormControl>

                    <FormControl error={this.state.language.error} style={styles.textField}>
                        <InputLabel>Language/Technology</InputLabel>

                        <Select
                            value={this.state.language.value}
                            onChange={this.handleLanguageChange}
                        >
                            <MenuItem value="">
                                <em>None</em>
                            </MenuItem>
                            {supportedLanguages.map(language => {
                                return (
                                    <MenuItem value={language}>{language}</MenuItem>
                                );
                            })}
                        </Select>

                        {this.state.language.error &&
                        <FormHelperText>{this.state.language.errorMsg}</FormHelperText>}
                    </FormControl>

                    <AceEditor
                        id="ace-editor"
                        mode={this.state.mode}
                        theme={editorTheme}
                        style={styles.aceEditor}
                        showPrintMargin={false}
                        tabSize={4}
                        wrapEnabled
                        value={this.state.code}
                        setOptions={aceConfig}
                        onChange={this.onCodeEditing}
                        onValidate={this.onEditorValidate}
                    />
                    <FormHelperText error={this.state.code.length > SNIPPET_CODE_LIMIT}
                                    style={styles.aceEditorCharCount}>
                        {this.state.code.length}/{SNIPPET_CODE_LIMIT}
                    </FormHelperText>

                    <div className={classes.wrapper}>
                        <Button
                            style={styles.postBtn}
                            variant="raised"
                            color="primary"
                            onClick={this.onClickSubmit}
                            disabled={this.state.loading}
                        >
                            {this.state.updating ? SNIPPET_UPDATE_BTN : SNIPPET_CREATE_BTN}
                        </Button>
                        {this.state.loading && <CircularProgress size={24} className={classes.buttonProgress} />}
                    </div>
                </div>
            </div>
        );
    }
}


function mapStateToProps(state) {
    return {
        auth: state.auth,
        snippets: state.snippets,
        router: state.router
    };
}


export default withRouter(connect(mapStateToProps, {
    createSnippet,
    showAlert,
    showBinaryAlert,
    resetOverridePath,
    closeBinaryAlert,
    updateSnippet,
    fetchSnippet
})(withStyles(materialStyles)(SnippetFormPage)));