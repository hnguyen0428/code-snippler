import React, { Component } from 'react';
import PropTypes from 'prop-types';

import IconButton from '@material-ui/core/IconButton';
import Input from '@material-ui/core/Input';

import Search from '@material-ui/icons/Search';


class SearchBar extends Component {
    constructor(props) {
        super(props);

        this.setState({
            focused: false
        });
    }

    handleSearch = () => {
        if (this.props.onClickSearch)
            this.props.onClickSearch(this.props.value);
    };

    onChange = event => {
        if (this.props.onChange)
            this.props.onChange(event);
    };

    onFocus = event => {
        this.setState({
            focused: true
        });
    };

    onBlur = event => {
        this.setState({
            focused: false
        });
    };

    onKeyDown = event => {
        if (this.state.focused && event.key === 'Enter' && this.props.onClickSearch)
            this.props.onClickSearch(this.props.value);
    };


    render() {
        return (
            <div style={this.props.style}>
                <Input
                    startAdornment={
                        <IconButton style={{color: this.props.color}} onClick={this.handleSearch}>
                            <Search/>
                        </IconButton>
                    }
                    disableUnderline={this.props.disableUnderline}
                    placeholder="Search for snippets"
                    style={{color: this.props.color}}
                    fullWidth={this.props.fullWidth}
                    value={this.props.value}
                    onChange={this.onChange}
                    onFocus={this.onFocus}
                    onKeyDown={this.onKeyDown}
                    onBlur={this.onBlur}
                >

                </Input>
            </div>
        );
    }
}


SearchBar.propTypes = {
    color: PropTypes.string,
    onClickSearch: PropTypes.func,
    onChange: PropTypes.func,
    style: PropTypes.object,
    fullWidth: PropTypes.bool,
    value: PropTypes.string,
    disableUnderline: PropTypes.bool
};

SearchBar.defaultProps = {
    color: 'white',
    fullWidth: true,
    disableUnderline: true
};


export default SearchBar;