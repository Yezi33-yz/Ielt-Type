// 文件路径：js/auth.js

/**
 * 1. 统一请求工具 (改名为 authFetch 以适配各页面)
 */
async function authFetch(url, options = {}) {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        window.location.href = 'login.html';
        return Promise.reject('未登录');
    }

    const authOptions = {
        ...options,
        headers: {
            'Content-Type': 'application/json', // 默认加上 JSON 格式
            ...options.headers,
            'Authorization': 'Bearer ' + token
        }
    };

    try {
        const response = await fetch(url, authOptions);
        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem('jwt_token');
            sessionStorage.removeItem('loginUser');
            window.location.href = 'login.html';
        }
        return response;
    } catch (error) {
        console.error("请求发生错误:", error);
        throw error;
    }
}

/**
 * 2. 🌟 全局 UI 初始化 (解决用户名“加载中”和头像问题)
 * 只要页面 header 结构一致，调用这个函数就能自动填好数据
 */
async function initGlobalUI() {
    const currentUser = sessionStorage.getItem('loginUser');
    if (!currentUser) return;

    // A. 填充名字
    const nameDisplay = document.getElementById('userNameDisplay');
    if (nameDisplay) nameDisplay.innerText = currentUser;

    // B. 填充头像 (取首字母并转大写)
    const avatar = document.getElementById('userAvatar');
    if (avatar) avatar.innerText = currentUser.charAt(0).toUpperCase();

    // C. 自动同步 VIP 状态
    const vipLabel = document.getElementById('vipLabel');
    if (vipLabel) {
        try {
            const res = await authFetch(`/api/auth/user/info?username=${currentUser}`);
            if (res.ok) {
                const user = await res.json();
                if (user.vip) {
                    vipLabel.innerText = '💎 VIP 会员';
                    vipLabel.classList.remove('text-yellow-600'); // 确保颜色正确
                    vipLabel.style.color = '#d97706';
                }
            }
        } catch (e) {
            console.warn("导航栏 VIP 状态加载失败", e);
        }
    }
}

/**
 * 3. 统一退出登录
 */
function logout() {
    if (confirm('确定要离开猫舍吗？')) {
        localStorage.removeItem('jwt_token');
        sessionStorage.removeItem('loginUser');
        window.location.href = 'login.html';
    }
}

// 为了向下兼容，保留 fetchWithAuth 别名（可选）
window.fetchWithAuth = authFetch;