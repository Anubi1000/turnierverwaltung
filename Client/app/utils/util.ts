export function getIdFromRoute(name: string): number {
  const route = useRoute();
  const id = parseInt(route.params[name] as string, 10);
  if (isNaN(id)) {
    throw createError({
      status: 400,
      statusMessage: `invalid ${name}`,
    });
  }
  return id;
}

export function provideIdFromRoute(name: string): number {
  const id = getIdFromRoute(name);
  provide(name, id);
  return id;
}

export function injectId(name: string): number {
  const id = inject<number>(name);
  if (id === undefined) {
    throw createError({
      status: 400,
      statusMessage: `${name} is not provided`,
    });
  }

  return id;
}
