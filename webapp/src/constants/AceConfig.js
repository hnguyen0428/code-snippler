
export const readAceConfig = {
    displayIndentGuides: false,
    behavioursEnabled: false,
    showGutter: true,
    showLineNumbers: true,
    showFoldWidgets: false
};

export const writeAceConfig = {
    displayIndentGuides: false,
    showGutter: true,
    showLineNumbers: true,
    enableBasicAutocompletion: true,
    enableLiveAutocompletion: true
};


export var editorTheme = localStorage.getItem("aceTheme") || "tomorrow";

export function setEditorTheme(theme) {
    editorTheme = theme;
}


export const supportedThemes = [
    'Ambiance',
    'Chaos',
    'Chrome',
    'Clouds',
    'Cobalt',
    'Dawn',
    'Dracula',
    'Eclipse',
    'GitHub',
    'Monokai',
    'Terminal',
    'Textmate',
    'Tomorrow',
    'Twilight',
    'Xcode',
];