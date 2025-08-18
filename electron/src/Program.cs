using ElectronCgi.DotNet;

namespace ArcGisMaps
{
    internal class Program
    {
        static void Main(string[] args)
        {
            var connection = new ConnectionBuilder().WithLogging().Build();

            connection.OnAsync("echo", async () =>
            {
                var call = new TaskCompletionSource<object>();

                call.SetResult("Hello World!");

                return call.Task;
            });

            connection.Listen();
        }
    }
}