
library(Cairo)
library(ggplot2)

Sys.setlocale("LC_ALL", "Portuguese")

CairoPNG(filename="all-datasets.png", width=750, height=750)
a <- read.csv(file='all-datasets.txt', head=FALSE, sep=",", fileEncoding="UTF-16LE")
a$V3 <- as.factor(a$V3)
a$V4 <- as.factor(a$V4)
ggplot(data=a, aes(x=V3, y=V2, colour=V4, group=V4, linetype=V4)) + geom_line() + geom_point() + xlab("Minutes") + ylab("Height")  + scale_linetype_discrete("Algorithm") + scale_colour_discrete("Algorithm") + ggtitle("Time test") + facet_wrap(~V1)
dev.off()