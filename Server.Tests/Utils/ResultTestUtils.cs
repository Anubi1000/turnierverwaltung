using AwesomeAssertions;
using AwesomeAssertions.Primitives;
using Microsoft.AspNetCore.Http;

namespace Turnierverwaltung.Server.Tests.Utils;

public static class ResultTestUtils
{
    public static AndWhichConstraint<ObjectAssertions, TExpected> BeResult<TResult, TExpected>(
        this ObjectAssertions assertions
    )
        where TResult : INestedHttpResult
        where TExpected : IResult => assertions.BeOfType<TResult>().Subject.Result.Should().BeOfType<TExpected>();
}
