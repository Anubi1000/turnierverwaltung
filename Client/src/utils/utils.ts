import type { AxiosResponse } from "axios";
import { computed, type ComputedRef, ref } from "vue";
import { useRoute } from "vue-router";

export function useRouterViewKey(level: number): ComputedRef<string> {
  const route = useRoute();

  return computed(() => {
    const matchedRoute = route.matched[level - 1];
    if (!matchedRoute) return "";

    const routeParams = [...matchedRoute.path.matchAll(/:(\w+)/g)].map(
      (match) => match[1],
    );

    if (routeParams.length === 0) return matchedRoute.path;

    let transformedRoute = matchedRoute.path;
    for (const routeParam of routeParams) {
      transformedRoute = transformedRoute.replace(
        routeParam,
        route.params[routeParam] as string,
      );
    }
    return transformedRoute;
  });
}

export function getIdFromRoute(name: string): number {
  const route = useRoute();
  const id = parseInt(route.params[name] as string, 10);
  if (isNaN(id)) {
    throw new Error("Could not get id from route");
  }
  return id;
}

export function useFetch<T>(fetchFn: () => Promise<AxiosResponse<T>>) {
  const data = ref<T | undefined>();
  const isLoading = ref(false);
  const hasError = ref(false);

  const execute = async () => {
    isLoading.value = true;
    try {
      const res = await fetchFn();
      data.value = res.data;
    } catch (_) {
      hasError.value = true;
    } finally {
      isLoading.value = false;
    }
  };

  execute();

  return {
    data,
    isLoading,
    hasError,
  };
}
