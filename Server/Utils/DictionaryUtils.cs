namespace Turnierverwaltung.Server.Utils;

public static class DictionaryUtils
{
    public static TValue GetOrCompute<TKey, TValue>(
        this Dictionary<TKey, TValue> dict,
        TKey key,
        Func<TKey, TValue> valueFactory
    )
        where TKey : notnull
    {
        if (dict.TryGetValue(key, out var value))
            return value;
        value = valueFactory(key);
        dict[key] = value;
        return value;
    }
}
