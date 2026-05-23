$jbr = "D:\IntelliJ IDEA Community Edition 2024.3.2.2\jbr"
$mvn = "D:\IntelliJ IDEA Community Edition 2024.3.2.2\plugins\maven\lib\maven3\bin\mvn.cmd"
$env:JAVA_HOME = $jbr
$env:MAVEN_OPTS = "-Xmx256m -Xms128m"
Write-Host "Compiling and starting server..." -ForegroundColor Cyan
& $mvn compile exec:java -D"exec.mainClass=org.database.api.ApiServer"
