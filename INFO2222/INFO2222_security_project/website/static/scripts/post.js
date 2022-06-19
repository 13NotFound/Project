function create() {
    const E = window.wangEditor
    if (E == null) return

    E.i18nChangeLanguage('en')

    E.Boot.registerModule(window.WangEditorPluginUploadAttachment.default)

    window.editor = E.createEditor({
        selector: '#editor-container',
        config: {
            placeholder: 'Type here...',
            hoverbarKeys: {
                attachment: {
                    menuKeys: ['downloadAttachment'],
                },
            },
            MENU_CONF: {
                uploadAttachment: {
                    server: '/upload',
                    fieldName: 'custom-fileName',
                    onInsertedAttachment(elem) {
                        console.log('inserted attachment ---- ', elem)
                    },
                }
            }
        }
    })
    window.toolbar = E.createToolbar({
        editor,
        selector: '#toolbar-container',
        config: {
            insertKeys: {
                index: 0,
                keys: ['uploadAttachment'],
            },
        }
    })
}
create()