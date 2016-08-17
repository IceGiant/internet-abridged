# [The Internet (Abridged): A Side Project](http://internet-abridged.com/)

## Purpose

This project is an in-progress news aggregator that I'm building as my own landing page for entertainment and keeping up on
programming and tech news.  You can see my running site [here](http://internet-abridged.com/) or click the link in the title.

[You can read the excellent gitbook and get a copy of the source I used as a seed here.](https://gitter.im/ochrons/scalajs-spa-tutorial?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

Built using [Scala.js](http://www.scala-js.org/) and [Play](https://www.playframework.com/).


### Scala IDE users

If you are using Scala IDE, you need to set additional settings to get your Eclipse project exported from SBT.

```
set EclipseKeys.skipParents in ThisBuild := false
eclipse
```
