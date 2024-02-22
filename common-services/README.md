# Common services

## Thumbnail service

In order to support additional image format, a specific listener must be put in the `web.xml` file

```xml
<listener>
    <display-name>ImageIO service provider loader/unloader</display-name>
    <listener-class>com.twelvemonkeys.servlet.image.IIOProviderContextListener</listener-class>
</listener>

```

For more details, see https://github.com/haraldk/TwelveMonkeys?tab=readme-ov-file#deploying-the-plugins-in-a-web-app .
