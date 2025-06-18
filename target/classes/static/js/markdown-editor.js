/**
 * MAPU文章管理系统 - Markdown编辑器
 * 提供Markdown编辑、预览和工具栏功能
 */

class MarkdownEditor {
    constructor(container, options = {}) {
        this.container = typeof container === 'string' ? document.querySelector(container) : container;
        this.options = {
            height: '400px',
            placeholder: '请输入文章内容...',
            toolbar: true,
            preview: true,
            split: true,
            autosave: true,
            autosaveInterval: 30000, // 30秒
            uploadUrl: '/api/upload/image',
            ...options
        };
        
        this.content = '';
        this.mode = 'edit'; // edit, preview, split
        this.autosaveTimer = null;
        this.isFullscreen = false;
        
        this.init();
    }
    
    init() {
        this.createEditor();
        this.bindEvents();
        if (this.options.autosave) {
            this.startAutosave();
        }
    }
    
    createEditor() {
        this.container.innerHTML = `
            <div class="markdown-editor" data-mode="${this.mode}">
                ${this.options.toolbar ? this.createToolbar() : ''}
                <div class="editor-container" style="height: ${this.options.height}">
                    <div class="editor-panel">
                        <textarea class="editor-textarea" placeholder="${this.options.placeholder}"></textarea>
                    </div>
                    ${this.options.preview ? this.createPreviewPanel() : ''}
                </div>
                <div class="editor-status">
                    <span class="word-count">字数: 0</span>
                    <span class="save-status"></span>
                </div>
            </div>
        `;
        
        this.textarea = this.container.querySelector('.editor-textarea');
        this.previewPanel = this.container.querySelector('.preview-panel');
        this.wordCountEl = this.container.querySelector('.word-count');
        this.saveStatusEl = this.container.querySelector('.save-status');
        
        this.addStyles();
    }
    
    createToolbar() {
        return `
            <div class="editor-toolbar">
                <div class="toolbar-group">
                    <button type="button" class="toolbar-btn" data-action="bold" title="粗体 (Ctrl+B)">
                        <i class="fas fa-bold"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="italic" title="斜体 (Ctrl+I)">
                        <i class="fas fa-italic"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="strikethrough" title="删除线">
                        <i class="fas fa-strikethrough"></i>
                    </button>
                </div>
                
                <div class="toolbar-group">
                    <button type="button" class="toolbar-btn" data-action="heading" title="标题">
                        <i class="fas fa-heading"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="quote" title="引用">
                        <i class="fas fa-quote-left"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="code" title="代码">
                        <i class="fas fa-code"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="codeblock" title="代码块">
                        <i class="fas fa-file-code"></i>
                    </button>
                </div>
                
                <div class="toolbar-group">
                    <button type="button" class="toolbar-btn" data-action="ul" title="无序列表">
                        <i class="fas fa-list-ul"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="ol" title="有序列表">
                        <i class="fas fa-list-ol"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="table" title="表格">
                        <i class="fas fa-table"></i>
                    </button>
                </div>
                
                <div class="toolbar-group">
                    <button type="button" class="toolbar-btn" data-action="link" title="链接 (Ctrl+K)">
                        <i class="fas fa-link"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="image" title="图片">
                        <i class="fas fa-image"></i>
                    </button>
                    <input type="file" class="image-upload" accept="image/*" style="display: none;">
                </div>
                
                <div class="toolbar-group">
                    <button type="button" class="toolbar-btn" data-action="hr" title="分割线">
                        <i class="fas fa-minus"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="undo" title="撤销 (Ctrl+Z)">
                        <i class="fas fa-undo"></i>
                    </button>
                    <button type="button" class="toolbar-btn" data-action="redo" title="重做 (Ctrl+Y)">
                        <i class="fas fa-redo"></i>
                    </button>
                </div>
                
                <div class="toolbar-group ms-auto">
                    ${this.options.preview ? `
                        <button type="button" class="toolbar-btn mode-btn" data-mode="edit" title="编辑模式">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button type="button" class="toolbar-btn mode-btn" data-mode="preview" title="预览模式">
                            <i class="fas fa-eye"></i>
                        </button>
                        ${this.options.split ? `
                            <button type="button" class="toolbar-btn mode-btn" data-mode="split" title="分屏模式">
                                <i class="fas fa-columns"></i>
                            </button>
                        ` : ''}
                    ` : ''}
                    <button type="button" class="toolbar-btn" data-action="fullscreen" title="全屏 (F11)">
                        <i class="fas fa-expand"></i>
                    </button>
                </div>
            </div>
        `;
    }
    
    createPreviewPanel() {
        return `
            <div class="preview-panel">
                <div class="preview-content"></div>
            </div>
        `;
    }
    
    addStyles() {
        if (document.getElementById('markdown-editor-styles')) return;
        
        const style = document.createElement('style');
        style.id = 'markdown-editor-styles';
        style.textContent = `
            .markdown-editor {
                border: 1px solid #ddd;
                border-radius: 8px;
                overflow: hidden;
                background: white;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            
            .editor-toolbar {
                display: flex;
                align-items: center;
                padding: 8px 12px;
                background: #f8f9fa;
                border-bottom: 1px solid #ddd;
                flex-wrap: wrap;
                gap: 8px;
            }
            
            .toolbar-group {
                display: flex;
                align-items: center;
                gap: 4px;
            }
            
            .toolbar-group:not(:last-child)::after {
                content: '';
                width: 1px;
                height: 20px;
                background: #ddd;
                margin-left: 8px;
            }
            
            .toolbar-btn {
                width: 32px;
                height: 32px;
                border: none;
                background: transparent;
                border-radius: 4px;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #666;
                transition: all 0.2s ease;
            }
            
            .toolbar-btn:hover {
                background: #e9ecef;
                color: #333;
            }
            
            .toolbar-btn.active {
                background: #007bff;
                color: white;
            }
            
            .editor-container {
                display: flex;
                position: relative;
            }
            
            .editor-panel {
                flex: 1;
                display: flex;
                flex-direction: column;
            }
            
            .editor-textarea {
                flex: 1;
                border: none;
                outline: none;
                padding: 16px;
                font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
                font-size: 14px;
                line-height: 1.6;
                resize: none;
                background: white;
                color: #333;
            }
            
            .preview-panel {
                flex: 1;
                border-left: 1px solid #ddd;
                background: white;
                overflow-y: auto;
            }
            
            .preview-content {
                padding: 16px;
                font-size: 14px;
                line-height: 1.6;
                color: #333;
            }
            
            .editor-status {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 8px 16px;
                background: #f8f9fa;
                border-top: 1px solid #ddd;
                font-size: 12px;
                color: #666;
            }
            
            .save-status {
                color: #28a745;
            }
            
            .save-status.saving {
                color: #ffc107;
            }
            
            .save-status.error {
                color: #dc3545;
            }
            
            /* 模式切换 */
            .markdown-editor[data-mode="edit"] .preview-panel {
                display: none;
            }
            
            .markdown-editor[data-mode="preview"] .editor-panel {
                display: none;
            }
            
            .markdown-editor[data-mode="split"] .editor-panel,
            .markdown-editor[data-mode="split"] .preview-panel {
                display: flex;
            }
            
            /* 全屏模式 */
            .markdown-editor.fullscreen {
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                z-index: 9999;
                border-radius: 0;
            }
            
            .markdown-editor.fullscreen .editor-container {
                height: calc(100vh - 120px);
            }
            
            /* 响应式设计 */
            @media (max-width: 768px) {
                .markdown-editor[data-mode="split"] .preview-panel {
                    display: none;
                }
                
                .toolbar-group {
                    gap: 2px;
                }
                
                .toolbar-btn {
                    width: 28px;
                    height: 28px;
                }
            }
            
            /* Markdown预览样式 */
            .preview-content h1,
            .preview-content h2,
            .preview-content h3,
            .preview-content h4,
            .preview-content h5,
            .preview-content h6 {
                margin-top: 24px;
                margin-bottom: 16px;
                font-weight: 600;
                line-height: 1.25;
            }
            
            .preview-content h1 {
                font-size: 2em;
                border-bottom: 1px solid #eaecef;
                padding-bottom: 8px;
            }
            
            .preview-content h2 {
                font-size: 1.5em;
                border-bottom: 1px solid #eaecef;
                padding-bottom: 8px;
            }
            
            .preview-content p {
                margin-bottom: 16px;
            }
            
            .preview-content blockquote {
                padding: 0 16px;
                color: #6a737d;
                border-left: 4px solid #dfe2e5;
                margin: 16px 0;
            }
            
            .preview-content code {
                padding: 2px 4px;
                background: #f6f8fa;
                border-radius: 3px;
                font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
                font-size: 85%;
            }
            
            .preview-content pre {
                padding: 16px;
                background: #f6f8fa;
                border-radius: 6px;
                overflow-x: auto;
                margin: 16px 0;
            }
            
            .preview-content pre code {
                padding: 0;
                background: transparent;
                border-radius: 0;
            }
            
            .preview-content ul,
            .preview-content ol {
                padding-left: 24px;
                margin-bottom: 16px;
            }
            
            .preview-content li {
                margin-bottom: 4px;
            }
            
            .preview-content table {
                border-collapse: collapse;
                width: 100%;
                margin: 16px 0;
            }
            
            .preview-content th,
            .preview-content td {
                border: 1px solid #dfe2e5;
                padding: 8px 12px;
                text-align: left;
            }
            
            .preview-content th {
                background: #f6f8fa;
                font-weight: 600;
            }
            
            .preview-content img {
                max-width: 100%;
                height: auto;
                border-radius: 4px;
                margin: 8px 0;
            }
            
            .preview-content hr {
                border: none;
                border-top: 1px solid #dfe2e5;
                margin: 24px 0;
            }
            
            .preview-content a {
                color: #0366d6;
                text-decoration: none;
            }
            
            .preview-content a:hover {
                text-decoration: underline;
            }
        `;
        
        document.head.appendChild(style);
    }
    
    bindEvents() {
        // 工具栏按钮事件
        this.container.addEventListener('click', (e) => {
            const btn = e.target.closest('.toolbar-btn');
            if (!btn) return;
            
            const action = btn.dataset.action;
            const mode = btn.dataset.mode;
            
            if (action) {
                this.executeAction(action);
            } else if (mode) {
                this.setMode(mode);
            }
        });
        
        // 文本区域事件
        this.textarea.addEventListener('input', () => {
            this.content = this.textarea.value;
            this.updatePreview();
            this.updateWordCount();
            this.markUnsaved();
        });
        
        // 键盘快捷键
        this.textarea.addEventListener('keydown', (e) => {
            this.handleKeydown(e);
        });
        
        // 拖拽上传
        this.textarea.addEventListener('dragover', (e) => {
            e.preventDefault();
            this.textarea.classList.add('drag-over');
        });
        
        this.textarea.addEventListener('dragleave', () => {
            this.textarea.classList.remove('drag-over');
        });
        
        this.textarea.addEventListener('drop', (e) => {
            e.preventDefault();
            this.textarea.classList.remove('drag-over');
            
            const files = Array.from(e.dataTransfer.files);
            const imageFiles = files.filter(file => file.type.startsWith('image/'));
            
            if (imageFiles.length > 0) {
                this.uploadImages(imageFiles);
            }
        });
        
        // 图片上传
        const imageUpload = this.container.querySelector('.image-upload');
        if (imageUpload) {
            imageUpload.addEventListener('change', (e) => {
                const files = Array.from(e.target.files);
                if (files.length > 0) {
                    this.uploadImages(files);
                }
                e.target.value = ''; // 清空文件选择
            });
        }
        
        // 全屏模式ESC键退出
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isFullscreen) {
                this.toggleFullscreen();
            }
        });
    }
    
    executeAction(action) {
        const textarea = this.textarea;
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const selectedText = textarea.value.substring(start, end);
        
        let replacement = '';
        let cursorOffset = 0;
        
        switch (action) {
            case 'bold':
                replacement = `**${selectedText || '粗体文本'}**`;
                cursorOffset = selectedText ? 0 : -4;
                break;
                
            case 'italic':
                replacement = `*${selectedText || '斜体文本'}*`;
                cursorOffset = selectedText ? 0 : -3;
                break;
                
            case 'strikethrough':
                replacement = `~~${selectedText || '删除线文本'}~~`;
                cursorOffset = selectedText ? 0 : -6;
                break;
                
            case 'heading':
                replacement = `# ${selectedText || '标题'}`;
                cursorOffset = selectedText ? 0 : -2;
                break;
                
            case 'quote':
                replacement = `> ${selectedText || '引用文本'}`;
                cursorOffset = selectedText ? 0 : -4;
                break;
                
            case 'code':
                replacement = `\`${selectedText || '代码'}\``;
                cursorOffset = selectedText ? 0 : -3;
                break;
                
            case 'codeblock':
                replacement = `\`\`\`\n${selectedText || '代码块'}\n\`\`\``;
                cursorOffset = selectedText ? 0 : -7;
                break;
                
            case 'ul':
                replacement = `- ${selectedText || '列表项'}`;
                cursorOffset = selectedText ? 0 : -3;
                break;
                
            case 'ol':
                replacement = `1. ${selectedText || '列表项'}`;
                cursorOffset = selectedText ? 0 : -3;
                break;
                
            case 'table':
                replacement = `| 列1 | 列2 | 列3 |\n|-----|-----|-----|\n| 内容 | 内容 | 内容 |`;
                break;
                
            case 'link':
                const url = prompt('请输入链接地址:', 'https://');
                if (url) {
                    replacement = `[${selectedText || '链接文本'}](${url})`;
                    cursorOffset = selectedText ? 0 : -url.length - 3;
                }
                break;
                
            case 'image':
                this.container.querySelector('.image-upload').click();
                return;
                
            case 'hr':
                replacement = '\n---\n';
                break;
                
            case 'undo':
                document.execCommand('undo');
                return;
                
            case 'redo':
                document.execCommand('redo');
                return;
                
            case 'fullscreen':
                this.toggleFullscreen();
                return;
        }
        
        if (replacement) {
            this.insertText(replacement, cursorOffset);
        }
    }
    
    insertText(text, cursorOffset = 0) {
        const textarea = this.textarea;
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        
        const before = textarea.value.substring(0, start);
        const after = textarea.value.substring(end);
        
        textarea.value = before + text + after;
        
        const newCursorPos = start + text.length + cursorOffset;
        textarea.setSelectionRange(newCursorPos, newCursorPos);
        
        textarea.focus();
        
        // 触发input事件
        textarea.dispatchEvent(new Event('input'));
    }
    
    handleKeydown(e) {
        // Ctrl+B - 粗体
        if (e.ctrlKey && e.key === 'b') {
            e.preventDefault();
            this.executeAction('bold');
        }
        // Ctrl+I - 斜体
        else if (e.ctrlKey && e.key === 'i') {
            e.preventDefault();
            this.executeAction('italic');
        }
        // Ctrl+K - 链接
        else if (e.ctrlKey && e.key === 'k') {
            e.preventDefault();
            this.executeAction('link');
        }
        // F11 - 全屏
        else if (e.key === 'F11') {
            e.preventDefault();
            this.toggleFullscreen();
        }
        // Tab - 缩进
        else if (e.key === 'Tab') {
            e.preventDefault();
            this.insertText('    ');
        }
    }
    
    setMode(mode) {
        this.mode = mode;
        this.container.querySelector('.markdown-editor').dataset.mode = mode;
        
        // 更新按钮状态
        this.container.querySelectorAll('.mode-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.mode === mode);
        });
        
        // 如果切换到预览模式，更新预览内容
        if (mode === 'preview' || mode === 'split') {
            this.updatePreview();
        }
    }
    
    updatePreview() {
        if (!this.previewPanel) return;
        
        const content = this.textarea.value;
        const html = this.markdownToHtml(content);
        this.previewPanel.querySelector('.preview-content').innerHTML = html;
    }
    
    markdownToHtml(markdown) {
        // 简单的Markdown转HTML实现
        let html = markdown
            // 转义HTML
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            
            // 标题
            .replace(/^### (.*$)/gim, '<h3>$1</h3>')
            .replace(/^## (.*$)/gim, '<h2>$1</h2>')
            .replace(/^# (.*$)/gim, '<h1>$1</h1>')
            
            // 粗体和斜体
            .replace(/\*\*\*(.*?)\*\*\*/g, '<strong><em>$1</em></strong>')
            .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
            .replace(/\*(.*?)\*/g, '<em>$1</em>')
            
            // 删除线
            .replace(/~~(.*?)~~/g, '<del>$1</del>')
            
            // 代码
            .replace(/`([^`]+)`/g, '<code>$1</code>')
            
            // 链接
            .replace(/\[([^\]]+)\]\(([^\)]+)\)/g, '<a href="$2" target="_blank">$1</a>')
            
            // 图片
            .replace(/!\[([^\]]*)\]\(([^\)]+)\)/g, '<img src="$2" alt="$1" />')
            
            // 分割线
            .replace(/^---$/gm, '<hr>')
            
            // 段落
            .replace(/\n\n/g, '</p><p>')
            .replace(/^(.+)$/gm, '<p>$1</p>')
            
            // 清理空段落
            .replace(/<p><\/p>/g, '')
            .replace(/<p>(<h[1-6]>.*?<\/h[1-6]>)<\/p>/g, '$1')
            .replace(/<p>(<hr>)<\/p>/g, '$1');
            
        // 处理代码块
        html = html.replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>');
        
        // 处理引用
        html = html.replace(/^&gt; (.+)$/gm, '<blockquote><p>$1</p></blockquote>');
        
        // 处理列表
        html = html.replace(/^- (.+)$/gm, '<li>$1</li>');
        html = html.replace(/(<li>.*<\/li>)/s, '<ul>$1</ul>');
        
        html = html.replace(/^\d+\. (.+)$/gm, '<li>$1</li>');
        html = html.replace(/(<li>.*<\/li>)/s, '<ol>$1</ol>');
        
        return html;
    }
    
    updateWordCount() {
        const text = this.textarea.value;
        const wordCount = text.length;
        this.wordCountEl.textContent = `字数: ${wordCount}`;
    }
    
    toggleFullscreen() {
        this.isFullscreen = !this.isFullscreen;
        this.container.querySelector('.markdown-editor').classList.toggle('fullscreen', this.isFullscreen);
        
        const icon = this.container.querySelector('[data-action="fullscreen"] i');
        icon.className = this.isFullscreen ? 'fas fa-compress' : 'fas fa-expand';
    }
    
    async uploadImages(files) {
        for (const file of files) {
            try {
                const formData = new FormData();
                formData.append('file', file);
                
                this.showSaveStatus('上传中...', 'saving');
                
                const response = await Http.upload(this.options.uploadUrl, formData, (progress) => {
                    this.showSaveStatus(`上传中... ${Math.round(progress)}%`, 'saving');
                });
                
                if (response.success) {
                    const imageMarkdown = `![${file.name}](${response.data.url})`;
                    this.insertText(imageMarkdown);
                    this.showSaveStatus('上传成功', 'success');
                } else {
                    throw new Error(response.message || '上传失败');
                }
            } catch (error) {
                console.error('图片上传失败:', error);
                this.showSaveStatus('上传失败', 'error');
                Toast.error(`图片上传失败: ${error.message}`);
            }
        }
    }
    
    startAutosave() {
        if (this.autosaveTimer) {
            clearInterval(this.autosaveTimer);
        }
        
        this.autosaveTimer = setInterval(() => {
            if (this.hasUnsavedChanges) {
                this.save();
            }
        }, this.options.autosaveInterval);
    }
    
    stopAutosave() {
        if (this.autosaveTimer) {
            clearInterval(this.autosaveTimer);
            this.autosaveTimer = null;
        }
    }
    
    markUnsaved() {
        this.hasUnsavedChanges = true;
    }
    
    markSaved() {
        this.hasUnsavedChanges = false;
    }
    
    async save() {
        if (!this.hasUnsavedChanges) return;
        
        try {
            this.showSaveStatus('保存中...', 'saving');
            
            // 这里应该调用实际的保存API
            // await Http.post('/api/article/autosave', { content: this.content });
            
            // 模拟保存
            await new Promise(resolve => setTimeout(resolve, 500));
            
            this.markSaved();
            this.showSaveStatus('已保存', 'success');
        } catch (error) {
            console.error('自动保存失败:', error);
            this.showSaveStatus('保存失败', 'error');
        }
    }
    
    showSaveStatus(message, type = 'info') {
        this.saveStatusEl.textContent = message;
        this.saveStatusEl.className = `save-status ${type}`;
        
        if (type === 'success') {
            setTimeout(() => {
                this.saveStatusEl.textContent = '';
                this.saveStatusEl.className = 'save-status';
            }, 2000);
        }
    }
    
    getValue() {
        return this.textarea.value;
    }
    
    setValue(value) {
        this.textarea.value = value;
        this.content = value;
        this.updatePreview();
        this.updateWordCount();
    }
    
    focus() {
        this.textarea.focus();
    }
    
    destroy() {
        this.stopAutosave();
        this.container.innerHTML = '';
    }
}

// 导出到全局
window.MarkdownEditor = MarkdownEditor;