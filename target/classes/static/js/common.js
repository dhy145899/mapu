/**
 * MAPU文章管理系统 - 公共JavaScript脚本
 * 包含通用功能、工具函数和全局配置
 */

// 全局配置
const CONFIG = {
    API_BASE_URL: '/api',
    UPLOAD_MAX_SIZE: 5 * 1024 * 1024, // 5MB
    ALLOWED_IMAGE_TYPES: ['image/jpeg', 'image/png', 'image/gif', 'image/webp'],
    PAGINATION_SIZE: 10,
    DEBOUNCE_DELAY: 300,
    TOAST_DURATION: 3000
};

// 工具函数
const Utils = {
    /**
     * 防抖函数
     * @param {Function} func 要执行的函数
     * @param {number} delay 延迟时间
     * @returns {Function} 防抖后的函数
     */
    debounce(func, delay = CONFIG.DEBOUNCE_DELAY) {
        let timeoutId;
        return function (...args) {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => func.apply(this, args), delay);
        };
    },

    /**
     * 节流函数
     * @param {Function} func 要执行的函数
     * @param {number} delay 延迟时间
     * @returns {Function} 节流后的函数
     */
    throttle(func, delay = CONFIG.DEBOUNCE_DELAY) {
        let lastCall = 0;
        return function (...args) {
            const now = Date.now();
            if (now - lastCall >= delay) {
                lastCall = now;
                func.apply(this, args);
            }
        };
    },

    /**
     * 格式化日期
     * @param {Date|string} date 日期对象或字符串
     * @param {string} format 格式化模式
     * @returns {string} 格式化后的日期字符串
     */
    formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
        if (!date) return '';
        const d = new Date(date);
        if (isNaN(d.getTime())) return '';

        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        const hours = String(d.getHours()).padStart(2, '0');
        const minutes = String(d.getMinutes()).padStart(2, '0');
        const seconds = String(d.getSeconds()).padStart(2, '0');

        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds);
    },

    /**
     * 相对时间格式化
     * @param {Date|string} date 日期
     * @returns {string} 相对时间字符串
     */
    timeAgo(date) {
        if (!date) return '';
        const now = new Date();
        const target = new Date(date);
        const diff = now - target;
        const seconds = Math.floor(diff / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);
        const months = Math.floor(days / 30);
        const years = Math.floor(months / 12);

        if (years > 0) return `${years}年前`;
        if (months > 0) return `${months}个月前`;
        if (days > 0) return `${days}天前`;
        if (hours > 0) return `${hours}小时前`;
        if (minutes > 0) return `${minutes}分钟前`;
        return '刚刚';
    },

    /**
     * 文件大小格式化
     * @param {number} bytes 字节数
     * @returns {string} 格式化后的文件大小
     */
    formatFileSize(bytes) {
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },

    /**
     * 生成随机ID
     * @param {number} length ID长度
     * @returns {string} 随机ID
     */
    generateId(length = 8) {
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        let result = '';
        for (let i = 0; i < length; i++) {
            result += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return result;
    },

    /**
     * 深拷贝对象
     * @param {any} obj 要拷贝的对象
     * @returns {any} 拷贝后的对象
     */
    deepClone(obj) {
        if (obj === null || typeof obj !== 'object') return obj;
        if (obj instanceof Date) return new Date(obj.getTime());
        if (obj instanceof Array) return obj.map(item => this.deepClone(item));
        if (typeof obj === 'object') {
            const cloned = {};
            Object.keys(obj).forEach(key => {
                cloned[key] = this.deepClone(obj[key]);
            });
            return cloned;
        }
    },

    /**
     * 验证邮箱格式
     * @param {string} email 邮箱地址
     * @returns {boolean} 是否有效
     */
    isValidEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },

    /**
     * 验证手机号格式
     * @param {string} phone 手机号
     * @returns {boolean} 是否有效
     */
    isValidPhone(phone) {
        const regex = /^1[3-9]\d{9}$/;
        return regex.test(phone);
    },

    /**
     * 验证密码强度
     * @param {string} password 密码
     * @returns {object} 验证结果
     */
    validatePassword(password) {
        const result = {
            isValid: false,
            strength: 0,
            messages: []
        };

        if (!password) {
            result.messages.push('密码不能为空');
            return result;
        }

        if (password.length < 6) {
            result.messages.push('密码长度至少6位');
        } else {
            result.strength += 1;
        }

        if (/[a-z]/.test(password)) result.strength += 1;
        if (/[A-Z]/.test(password)) result.strength += 1;
        if (/\d/.test(password)) result.strength += 1;
        if (/[^\w\s]/.test(password)) result.strength += 1;

        if (result.strength >= 3) {
            result.isValid = true;
        } else {
            result.messages.push('密码应包含大小写字母、数字和特殊字符');
        }

        return result;
    },

    /**
     * 转义HTML字符
     * @param {string} text 文本
     * @returns {string} 转义后的文本
     */
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    /**
     * 截取文本
     * @param {string} text 文本
     * @param {number} length 长度
     * @param {string} suffix 后缀
     * @returns {string} 截取后的文本
     */
    truncateText(text, length = 100, suffix = '...') {
        if (!text || text.length <= length) return text;
        return text.substring(0, length) + suffix;
    }
};

// HTTP请求工具
const Http = {
    /**
     * 发送GET请求
     * @param {string} url 请求URL
     * @param {object} params 查询参数
     * @returns {Promise} 请求Promise
     */
    async get(url, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const fullUrl = queryString ? `${url}?${queryString}` : url;
        
        try {
            const response = await fetch(fullUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'same-origin'
            });
            return await this.handleResponse(response);
        } catch (error) {
            throw new Error(`请求失败: ${error.message}`);
        }
    },

    /**
     * 发送POST请求
     * @param {string} url 请求URL
     * @param {object} data 请求数据
     * @returns {Promise} 请求Promise
     */
    async post(url, data = {}) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'same-origin',
                body: JSON.stringify(data)
            });
            return await this.handleResponse(response);
        } catch (error) {
            throw new Error(`请求失败: ${error.message}`);
        }
    },

    /**
     * 发送PUT请求
     * @param {string} url 请求URL
     * @param {object} data 请求数据
     * @returns {Promise} 请求Promise
     */
    async put(url, data = {}) {
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'same-origin',
                body: JSON.stringify(data)
            });
            return await this.handleResponse(response);
        } catch (error) {
            throw new Error(`请求失败: ${error.message}`);
        }
    },

    /**
     * 发送DELETE请求
     * @param {string} url 请求URL
     * @returns {Promise} 请求Promise
     */
    async delete(url) {
        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'same-origin'
            });
            return await this.handleResponse(response);
        } catch (error) {
            throw new Error(`请求失败: ${error.message}`);
        }
    },

    /**
     * 上传文件
     * @param {string} url 上传URL
     * @param {FormData} formData 表单数据
     * @param {Function} onProgress 进度回调
     * @returns {Promise} 上传Promise
     */
    async upload(url, formData, onProgress = null) {
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            
            if (onProgress) {
                xhr.upload.addEventListener('progress', (e) => {
                    if (e.lengthComputable) {
                        const percentComplete = (e.loaded / e.total) * 100;
                        onProgress(percentComplete);
                    }
                });
            }
            
            xhr.addEventListener('load', async () => {
                if (xhr.status >= 200 && xhr.status < 300) {
                    try {
                        const response = JSON.parse(xhr.responseText);
                        resolve(response);
                    } catch (error) {
                        resolve(xhr.responseText);
                    }
                } else {
                    reject(new Error(`上传失败: ${xhr.statusText}`));
                }
            });
            
            xhr.addEventListener('error', () => {
                reject(new Error('上传失败'));
            });
            
            xhr.open('POST', url);
            xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
            xhr.send(formData);
        });
    },

    /**
     * 处理响应
     * @param {Response} response 响应对象
     * @returns {Promise} 处理后的数据
     */
    async handleResponse(response) {
        if (!response.ok) {
            const error = await response.text();
            throw new Error(`HTTP ${response.status}: ${error}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        
        return await response.text();
    }
};

// 消息提示工具
const Toast = {
    /**
     * 显示成功消息
     * @param {string} message 消息内容
     * @param {number} duration 显示时长
     */
    success(message, duration = CONFIG.TOAST_DURATION) {
        this.show(message, 'success', duration);
    },

    /**
     * 显示错误消息
     * @param {string} message 消息内容
     * @param {number} duration 显示时长
     */
    error(message, duration = CONFIG.TOAST_DURATION) {
        this.show(message, 'error', duration);
    },

    /**
     * 显示警告消息
     * @param {string} message 消息内容
     * @param {number} duration 显示时长
     */
    warning(message, duration = CONFIG.TOAST_DURATION) {
        this.show(message, 'warning', duration);
    },

    /**
     * 显示信息消息
     * @param {string} message 消息内容
     * @param {number} duration 显示时长
     */
    info(message, duration = CONFIG.TOAST_DURATION) {
        this.show(message, 'info', duration);
    },

    /**
     * 显示消息
     * @param {string} message 消息内容
     * @param {string} type 消息类型
     * @param {number} duration 显示时长
     */
    show(message, type = 'info', duration = CONFIG.TOAST_DURATION) {
        // 创建toast容器（如果不存在）
        let container = document.getElementById('toast-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toast-container';
            container.style.cssText = `
                position: fixed;
                top: 20px;
                right: 20px;
                z-index: 9999;
                max-width: 350px;
            `;
            document.body.appendChild(container);
        }

        // 创建toast元素
        const toast = document.createElement('div');
        const toastId = Utils.generateId();
        toast.id = toastId;
        
        const typeColors = {
            success: '#28a745',
            error: '#dc3545',
            warning: '#ffc107',
            info: '#17a2b8'
        };
        
        const typeIcons = {
            success: '✓',
            error: '✕',
            warning: '⚠',
            info: 'ℹ'
        };

        toast.style.cssText = `
            background: white;
            border-left: 4px solid ${typeColors[type]};
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            margin-bottom: 10px;
            padding: 16px;
            display: flex;
            align-items: center;
            animation: slideInRight 0.3s ease-out;
            cursor: pointer;
        `;

        toast.innerHTML = `
            <div style="
                width: 20px;
                height: 20px;
                border-radius: 50%;
                background: ${typeColors[type]};
                color: white;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 12px;
                font-weight: bold;
                margin-right: 12px;
                flex-shrink: 0;
            ">${typeIcons[type]}</div>
            <div style="flex: 1; color: #333; font-size: 14px; line-height: 1.4;">${message}</div>
            <div style="
                width: 20px;
                height: 20px;
                cursor: pointer;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #999;
                font-size: 16px;
                margin-left: 8px;
            " onclick="Toast.hide('${toastId}')">×</div>
        `;

        // 添加样式
        if (!document.getElementById('toast-styles')) {
            const style = document.createElement('style');
            style.id = 'toast-styles';
            style.textContent = `
                @keyframes slideInRight {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
                @keyframes slideOutRight {
                    from { transform: translateX(0); opacity: 1; }
                    to { transform: translateX(100%); opacity: 0; }
                }
            `;
            document.head.appendChild(style);
        }

        container.appendChild(toast);

        // 自动隐藏
        if (duration > 0) {
            setTimeout(() => this.hide(toastId), duration);
        }

        // 点击隐藏
        toast.addEventListener('click', () => this.hide(toastId));
    },

    /**
     * 隐藏消息
     * @param {string} toastId 消息ID
     */
    hide(toastId) {
        const toast = document.getElementById(toastId);
        if (toast) {
            toast.style.animation = 'slideOutRight 0.3s ease-in';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }
    },

    /**
     * 清除所有消息
     */
    clear() {
        const container = document.getElementById('toast-container');
        if (container) {
            container.innerHTML = '';
        }
    }
};

// 模态框工具
const Modal = {
    /**
     * 显示确认对话框
     * @param {string} title 标题
     * @param {string} message 消息
     * @param {Function} onConfirm 确认回调
     * @param {Function} onCancel 取消回调
     */
    confirm(title, message, onConfirm = null, onCancel = null) {
        const modalId = 'confirm-modal-' + Utils.generateId();
        const modalHtml = `
            <div class="modal fade" id="${modalId}" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">${title}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <p>${message}</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                            <button type="button" class="btn btn-primary" id="confirm-btn">确认</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        const modal = new bootstrap.Modal(document.getElementById(modalId));
        
        // 绑定事件
        document.getElementById('confirm-btn').addEventListener('click', () => {
            if (onConfirm) onConfirm();
            modal.hide();
        });

        document.getElementById(modalId).addEventListener('hidden.bs.modal', () => {
            if (onCancel) onCancel();
            document.getElementById(modalId).remove();
        });

        modal.show();
    },

    /**
     * 显示提示对话框
     * @param {string} title 标题
     * @param {string} message 消息
     * @param {Function} onClose 关闭回调
     */
    alert(title, message, onClose = null) {
        const modalId = 'alert-modal-' + Utils.generateId();
        const modalHtml = `
            <div class="modal fade" id="${modalId}" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">${title}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <p>${message}</p>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-primary" data-bs-dismiss="modal">确定</button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        const modal = new bootstrap.Modal(document.getElementById(modalId));
        
        document.getElementById(modalId).addEventListener('hidden.bs.modal', () => {
            if (onClose) onClose();
            document.getElementById(modalId).remove();
        });

        modal.show();
    }
};

// 本地存储工具
const Storage = {
    /**
     * 设置本地存储
     * @param {string} key 键
     * @param {any} value 值
     */
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('存储失败:', error);
        }
    },

    /**
     * 获取本地存储
     * @param {string} key 键
     * @param {any} defaultValue 默认值
     * @returns {any} 存储的值
     */
    get(key, defaultValue = null) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : defaultValue;
        } catch (error) {
            console.error('读取失败:', error);
            return defaultValue;
        }
    },

    /**
     * 删除本地存储
     * @param {string} key 键
     */
    remove(key) {
        try {
            localStorage.removeItem(key);
        } catch (error) {
            console.error('删除失败:', error);
        }
    },

    /**
     * 清空本地存储
     */
    clear() {
        try {
            localStorage.clear();
        } catch (error) {
            console.error('清空失败:', error);
        }
    }
};

// 图片处理工具
const ImageUtils = {
    /**
     * 压缩图片
     * @param {File} file 图片文件
     * @param {number} maxWidth 最大宽度
     * @param {number} maxHeight 最大高度
     * @param {number} quality 质量(0-1)
     * @returns {Promise<Blob>} 压缩后的图片
     */
    compress(file, maxWidth = 800, maxHeight = 600, quality = 0.8) {
        return new Promise((resolve) => {
            const canvas = document.createElement('canvas');
            const ctx = canvas.getContext('2d');
            const img = new Image();
            
            img.onload = () => {
                // 计算新尺寸
                let { width, height } = img;
                if (width > height) {
                    if (width > maxWidth) {
                        height = (height * maxWidth) / width;
                        width = maxWidth;
                    }
                } else {
                    if (height > maxHeight) {
                        width = (width * maxHeight) / height;
                        height = maxHeight;
                    }
                }
                
                canvas.width = width;
                canvas.height = height;
                
                // 绘制图片
                ctx.drawImage(img, 0, 0, width, height);
                
                // 转换为Blob
                canvas.toBlob(resolve, file.type, quality);
            };
            
            img.src = URL.createObjectURL(file);
        });
    },

    /**
     * 获取图片预览URL
     * @param {File} file 图片文件
     * @returns {string} 预览URL
     */
    getPreviewUrl(file) {
        return URL.createObjectURL(file);
    },

    /**
     * 验证图片文件
     * @param {File} file 文件
     * @returns {object} 验证结果
     */
    validate(file) {
        const result = {
            isValid: true,
            messages: []
        };

        if (!file) {
            result.isValid = false;
            result.messages.push('请选择文件');
            return result;
        }

        if (!CONFIG.ALLOWED_IMAGE_TYPES.includes(file.type)) {
            result.isValid = false;
            result.messages.push('不支持的图片格式');
        }

        if (file.size > CONFIG.UPLOAD_MAX_SIZE) {
            result.isValid = false;
            result.messages.push(`文件大小不能超过${Utils.formatFileSize(CONFIG.UPLOAD_MAX_SIZE)}`);
        }

        return result;
    }
};

// 页面加载完成后的初始化
document.addEventListener('DOMContentLoaded', function() {
    // 初始化工具提示
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // 初始化弹出框
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // 自动隐藏警告消息
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert.parentNode) {
                alert.style.transition = 'opacity 0.5s ease';
                alert.style.opacity = '0';
                setTimeout(() => {
                    if (alert.parentNode) {
                        alert.parentNode.removeChild(alert);
                    }
                }, 500);
            }
        }, 5000);
    });

    // 平滑滚动
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // 返回顶部按钮
    const backToTopBtn = document.createElement('button');
    backToTopBtn.innerHTML = '↑';
    backToTopBtn.style.cssText = `
        position: fixed;
        bottom: 30px;
        right: 30px;
        width: 50px;
        height: 50px;
        border-radius: 50%;
        background: var(--primary-color, #667eea);
        color: white;
        border: none;
        font-size: 20px;
        cursor: pointer;
        z-index: 1000;
        opacity: 0;
        visibility: hidden;
        transition: all 0.3s ease;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
    `;
    
    backToTopBtn.addEventListener('click', () => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
    
    document.body.appendChild(backToTopBtn);
    
    // 监听滚动显示/隐藏返回顶部按钮
    window.addEventListener('scroll', Utils.throttle(() => {
        if (window.pageYOffset > 300) {
            backToTopBtn.style.opacity = '1';
            backToTopBtn.style.visibility = 'visible';
        } else {
            backToTopBtn.style.opacity = '0';
            backToTopBtn.style.visibility = 'hidden';
        }
    }, 100));
});

// 导出到全局
window.Utils = Utils;
window.Http = Http;
window.Toast = Toast;
window.Modal = Modal;
window.Storage = Storage;
window.ImageUtils = ImageUtils;
window.CONFIG = CONFIG;