import { onMounted, onUnmounted, type Ref } from "vue";

export interface AutoScrollOptions {
  pxPerSecond?: number;
  waitTime?: number;
  iterations?: number;
  onComplete: () => void;
}

function debugLog(message: string) {
  // @ts-expect-error condition is only for build
  if (process.env.NODE_ENV === "development") {
    console.log(message);
  }
}

function debugWarn(message: string) {
  // @ts-expect-error condition is only for build
  if (process.env.NODE_ENV === "development") {
    console.warn(message);
  }
}

function delay(ms: number) {
  debugLog(`Delaying for ${ms}ms`);
  return new Promise((res) => setTimeout(res, ms));
}

function getMaxScrollTop(el: HTMLElement) {
  return el.scrollHeight - el.clientHeight;
}

function isScrollable(el: HTMLElement) {
  return getMaxScrollTop(el) > 0;
}

export function useAutoScroll(
  elementRef: Ref<HTMLElement | null>,
  options: AutoScrollOptions,
) {
  const {
    pxPerSecond = 1,
    waitTime = 4000,
    iterations = 1,
    onComplete,
  } = options;

  if (pxPerSecond <= 0) throw new Error("pxPerSeconds must be greater than 0.");

  let stop = false;
  let animationFrame: number | null = null;
  let currentIteration = 0;

  function scrollTo(element: HTMLElement, to: "top" | "bottom") {
    if (!isScrollable(element)) {
      debugLog(`Element is not scrollable skipping scroll.`);
      return;
    }

    debugLog(`Starting scroll to ${to} at ${pxPerSecond}px/s`);
    return new Promise((resolve) => {
      let start = element.scrollTop;
      let target = to === "top" ? 0 : getMaxScrollTop(element);

      let distance = target - start;
      let totalDistance = Math.abs(distance);

      let duration = (totalDistance / pxPerSecond) * 1000;
      let startTime = performance.now();

      debugLog(
        `Scroll from ${start}px to ${target}px over ${duration.toFixed(2)}ms`,
      );

      function animateScroll(currentTime: number) {
        if (stop) {
          debugLog("Scrolling stopped.");
          return;
        }

        const maxScrollTop = getMaxScrollTop(element);
        if (to === "bottom" && maxScrollTop !== target) {
          start = element.scrollTop;
          target = maxScrollTop;

          distance = target - start;
          totalDistance = Math.abs(distance);

          duration = (totalDistance / pxPerSecond) * 1000;
          startTime = currentTime;
        }

        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);

        element.scrollTop = start + distance * progress;

        debugLog(`Scrolling... ${progress.toFixed(2)} progress.`);

        if (progress < 1) {
          animationFrame = requestAnimationFrame(animateScroll);
        } else {
          debugLog("Scroll completed.");
          resolve(undefined);
        }
      }

      animationFrame = requestAnimationFrame(animateScroll);
    });
  }

  async function startScroll() {
    const el = elementRef.value;
    if (!el) {
      debugWarn("No element to scroll.");
      return;
    }

    for (
      currentIteration = 0;
      currentIteration < iterations;
      currentIteration++
    ) {
      debugLog(`Starting iteration ${currentIteration + 1} of ${iterations}`);

      await scrollTo(el, "bottom");
      await delay(waitTime);
      await scrollTo(el, "top");
      await delay(waitTime);
    }

    debugLog(`Scrolling done.`);
    onComplete();
  }

  onMounted(() => {
    debugLog("Component mounted. Starting scroll.");
    stop = false;
    startScroll();

    const el = elementRef.value;
    if (el) {
      debugLog("ResizeObserver attached.");
    }
  });

  onUnmounted(() => {
    debugLog("Component unmounted. Stopping scroll.");
    stop = true;
    if (animationFrame) {
      cancelAnimationFrame(animationFrame);
      debugLog("Animation frame cancelled.");
    }
  });
}
