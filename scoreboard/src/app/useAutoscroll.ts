import { RefObject, useEffect, useRef, useState } from "react";

export function useAutoscroll({
  msPerPixel,
  waitTime,
  numOfScrolls,
  onFinish,
  containerRef,
}: {
  containerRef: RefObject<HTMLDivElement>;
  msPerPixel: number;
  numOfScrolls: number;
  onFinish: () => void;
  waitTime: number;
}) {
  const [progress, setProgress] = useState(0);
  const scrollCount = useRef(0);
  const currentTimeout = useRef(-1);

  useEffect(() => {
    if (!containerRef.current) return; // Abort if no ref

    const container = containerRef.current;
    const scrollHeight = container.scrollHeight;
    const containerHeight = container.clientHeight;

    const needsToScroll = scrollHeight > containerHeight;
    if (!needsToScroll) {
      if (currentTimeout.current != -1) return;

      setProgress(1);

      // @ts-expect-error Timeout is number in browser
      currentTimeout.current = setTimeout(() => {
        onFinish();
        currentTimeout.current = -1;
      }, waitTime * 2);
      return;
    }

    const maxScrollTop = scrollHeight - containerHeight;

    let scrollToBottom = true;
    let shouldScroll = false;

    function setCurrentProgress() {
      const half = 1 / numOfScrolls;

      let total = half * scrollCount.current;

      if (shouldScroll) {
        let part = (container.scrollTop / maxScrollTop) * half;
        if (!scrollToBottom) part = half - part;
        total += part;
      }

      setProgress(total);
    }

    function scroll() {
      if (scrollToBottom) {
        container.scrollTop += 1;
      } else {
        container.scrollTop -= 1;
      }
    }

    setTimeout(() => {
      shouldScroll = true;
    }, waitTime);

    const interval = setInterval(() => {
      if (!shouldScroll) return;

      if (
        (container.scrollTop == 0 && !scrollToBottom) ||
        (container.scrollTop == maxScrollTop && scrollToBottom)
      ) {
        shouldScroll = false;
        scrollToBottom = !scrollToBottom;
        scrollCount.current += 1;

        setTimeout(() => {
          scroll();
          shouldScroll = true;
        }, waitTime);
      } else {
        if (scrollCount.current == numOfScrolls) {
          onFinish();
          return;
        }
        scroll();
        setCurrentProgress();
      }
    }, msPerPixel);

    return () => {
      clearInterval(interval);
    };
  }, [containerRef, msPerPixel, waitTime, numOfScrolls, onFinish]);

  return { progress };
}
