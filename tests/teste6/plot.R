
library(Cairo)
library(ggplot2)

Sys.setlocale("LC_ALL", "Portuguese")

CairoPNG(filename="teste6-fu.png", width=1000, height=1000)
a <- read.csv(file='fu-converted-100-all.txt', head=FALSE, sep=",")
a$V4 <- as.factor(a$V4)
a$V7 <- as.factor(a$V7)
ggplot(data=a, aes(x=V7, y=V8, colour=V4, group=V4, linetype=V4)) + geom_line() + geom_point() + xlab("Population") + ylab("Height")  + scale_linetype_discrete("Generations") + scale_colour_discrete("Generations") + ggtitle("Dataset fu") + facet_wrap(~V4)
dev.off()


CairoPNG(filename="teste6-poly1a.png", width=1000, height=1000)
a <- read.csv(file='poly1a-converted-100-all.txt', head=FALSE, sep=",")
a$V4 <- as.factor(a$V4)
a$V7 <- as.factor(a$V7)
ggplot(data=a, aes(x=V7, y=V8, colour=V4, group=V4, linetype=V4)) + geom_line() + geom_point() + xlab("Population") + ylab("Height")  + scale_linetype_discrete("Generations") + scale_colour_discrete("Generations") + ggtitle("Dataset poly1a") + facet_wrap(~V4)
dev.off()


CairoPNG(filename="teste6-poly2b.png", width=1000, height=1000)
a <- read.csv(file='poly2b-converted-100-all.txt', head=FALSE, sep=",")
a$V4 <- as.factor(a$V4)
a$V7 <- as.factor(a$V7)
ggplot(data=a, aes(x=V7, y=V8, colour=V4, group=V4, linetype=V4)) + geom_line() + geom_point() + xlab("Population") + ylab("Height")  + scale_linetype_discrete("Generations") + scale_colour_discrete("Generations") + ggtitle("Dataset poly2b") + facet_wrap(~V4)
dev.off()


CairoPNG(filename="teste6-poly3b.png", width=1000, height=1000)
a <- read.csv(file='poly3b-converted-100-all.txt', head=FALSE, sep=",")
a$V4 <- as.factor(a$V4)
a$V7 <- as.factor(a$V7)
ggplot(data=a, aes(x=V7, y=V8, colour=V4, group=V4, linetype=V4)) + geom_line() + geom_point() + xlab("Population") + ylab("Height")  + scale_linetype_discrete("Generations") + scale_colour_discrete("Generations") + ggtitle("Dataset poly3b") + facet_wrap(~V4)
dev.off()


CairoPNG(filename="teste6-poly4b.png", width=1000, height=1000)
a <- read.csv(file='poly4b-converted-100-all.txt', head=FALSE, sep=",")
a$V4 <- as.factor(a$V4)
a$V7 <- as.factor(a$V7)
ggplot(data=a, aes(x=V7, y=V8, colour=V4, group=V4, linetype=V4)) + geom_line() + geom_point() + xlab("Population") + ylab("Height")  + scale_linetype_discrete("Generations") + scale_colour_discrete("Generations") + ggtitle("Dataset poly4b") + facet_wrap(~V4)
dev.off()
