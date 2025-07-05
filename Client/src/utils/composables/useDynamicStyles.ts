import {
  onMounted,
  onUnmounted,
  type Ref,
  watchEffect,
  type WatchHandle,
} from "vue";

export function useDynamicStyles(css: Ref<string>) {
  let styleTag: HTMLStyleElement | null = null;
  let watchHandle: WatchHandle | null = null;

  onMounted(() => {
    styleTag = document.createElement("style");

    watchHandle = watchEffect(() => {
      if (styleTag) {
        styleTag.textContent = css.value;
      }
    });

    document.head.appendChild(styleTag);
  });

  onUnmounted(() => {
    if (watchHandle) {
      watchHandle.stop();
      watchHandle = null;
    }

    if (styleTag?.parentNode) {
      styleTag.parentNode.removeChild(styleTag);
      styleTag = null;
    }
  });
}
