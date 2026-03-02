<script setup lang="ts">
import { onLaunch, onShow, onHide } from "@dcloudio/uni-app";
import { useUserStore } from "@/stores/user";
import { useCartStore } from "@/stores/cart";

onLaunch(() => {
  console.log("App Launch");
  // 检查登录状态
  const userStore = useUserStore();
  if (userStore.isLoggedIn) {
    userStore.fetchUserInfo();
  }
});

onShow(() => {
  console.log("App Show");
  // 更新购物车数量
  const cartStore = useCartStore();
  if (useUserStore().isLoggedIn) {
    const count = cartStore.fetchCartCount();
    if (count > 0) {
      uni.setTabBarBadge({
        index: 2,
        text: count > 99 ? '99+' : String(count)
      });
    } else {
      uni.removeTabBarBadge({ index: 2 });
    }
  }
});

onHide(() => {
  console.log("App Hide");
});
</script>

<style lang="scss">
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-size: 28rpx;
  color: #333;
}

/* 主题色 */
$primary-color: #4CAF50;
$danger-color: #f44336;
$text-color: #333;
$text-light: #999;

/* 通用样式 */
.container {
  padding: 20rpx;
}

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flex-column {
  display: flex;
  flex-direction: column;
}

.flex-1 {
  flex: 1;
}

.text-center {
  text-align: center;
}

.text-right {
  text-align: right;
}

/* 按钮样式 */
.btn-primary {
  background-color: $primary-color;
  color: #fff;
  border: none;
  border-radius: 40rpx;
  padding: 20rpx 40rpx;
  font-size: 30rpx;
  
  &::after {
    border: none;
  }
}

.btn-primary:active {
  opacity: 0.8;
}

.btn-disabled {
  background-color: #ccc;
  color: #fff;
}

/* 价格样式 */
.price {
  color: $danger-color;
  font-weight: bold;
  
  &::before {
    content: '¥';
    font-size: 24rpx;
  }
}

/* 空状态 */
.empty-state {
  padding: 100rpx 0;
  text-align: center;
  color: $text-light;
  
  .empty-icon {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 20rpx;
  }
  
  .empty-text {
    font-size: 28rpx;
  }
}

/* 卡片样式 */
.card {
  background-color: #fff;
  border-radius: 16rpx;
  margin: 20rpx;
  padding: 20rpx;
}

/* 分割线 */
.divider {
  height: 1rpx;
  background-color: #eee;
  margin: 20rpx 0;
}
</style>
