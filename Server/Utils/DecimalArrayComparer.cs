namespace Turnierverwaltung.Server.Utils;

public class DecimalArrayComparer : IComparer<decimal[]>
{
    public int Compare(decimal[]? array1, decimal[]? array2)
    {
        // Ensure that neither array is null
        if (array1 == null && array2 == null)
            return 0;
        if (array1 == null)
            return -1;
        if (array2 == null)
            return 1;

        var maxLength = Math.Max(array1.Length, array2.Length);

        // Compare elements in both arrays, treating missing values as 0
        for (var i = 0; i < maxLength; i++)
        {
            var value1 = i < array1.Length ? array1[i] : 0;
            var value2 = i < array2.Length ? array2[i] : 0;

            var comparison = value1.CompareTo(value2);
            if (comparison != 0)
            {
                return comparison;
            }
        }

        return 0; // Arrays are equal when compared
    }
}
