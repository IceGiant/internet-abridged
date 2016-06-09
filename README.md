# The Internet (Abridged): A Side Project

[You can read the excellent gitbook and get a copy of the source code I used to make this here.](https://gitter.im/ochrons/scalajs-spa-tutorial?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

I built this using:
[Scala.js](http://www.scala-js.org/) and [Play](https://www.playframework.com/).

## Purpose

This project is an in-progress news aggregator that I'm building as my own landing page for entertainment and keeping up on
programming and tech news.  You can see my running site [here](http://internet-abridged.com/).


# Scala IDE users

If you are using Scala IDE, you need to set additional settings to get your Eclipse project exported from SBT.

```
set EclipseKeys.skipParents in ThisBuild := false
eclipse
```
