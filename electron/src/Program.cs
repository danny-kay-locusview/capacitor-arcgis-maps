using ElectronCgi.DotNet;

namespace ArcGisMaps
{
    internal class Program
    {
        static void Main(string[] args)
        {
            var connection = new ConnectionBuilder().WithLogging().Build();

            connection.OnAsync("signIn", async (dynamic options) =>
            {
                var call = new TaskCompletionSource<object>();

                call.SetResult("Signing In...");

                return call.Task;
            });

            connection.OnAsync("signOut", async () =>
            {
                var call = new TaskCompletionSource<object>();

                call.SetResult("Signing Out...");

                return call.Task;
            });

            connection.OnAsync("query", async (dynamic options) =>
            {
                var call = new TaskCompletionSource<object>();

                call.SetResult("Querying...");

                return call.Task;
            });

            connection.Listen();
        }
    }
}
