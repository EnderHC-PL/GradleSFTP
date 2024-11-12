# GradleSFTP
 
Simple and lightweight Gradle plugin which adds task to upload file via SFTP protocol.

### Example Usage:
```kotlin
import pl.enderhc.sftp.SFTPUploadPlugin.SFTPUploadTask // on the top of file

// somewhere in your build script (e.g build.gradle.kts)
tasks.register<SFTPUploadTask>("upload") {
    host = "127.0.0.1"
    port = 22
    username = "username"
    password = "password"

    val name = "My-Application-${project.version}.jar"
    localFile = file("build/libs/${name}")
    remotePath = "/home/Applications/${name}"
}
```

```
./gradlew upload - this command will perform task
```

### Adding plugin

**settings.gradle.kts:**
```kotlin
pluginManagement {
    repositories {
        maven("https://repo.enderhc.pl/releases/")
    }
}
```
> [!IMPORTANT]  
> Above repository is proprietary and access is restricted. The public repository will be available soon.

---
Special thanks to [JetBrains](https://www.jetbrains.com/products/) company for providing development tools used to develop this project.

[<img src="https://user-images.githubusercontent.com/65517973/210912946-447a6b9a-2685-4796-9482-a44bffc727ce.png" alt="JetBrains" width="150">](https://www.jetbrains.com)
