
library(Cairo)
library(ggplot2)

Sys.setlocale("LC_ALL", "Portuguese")

CairoPNG(filename="teste5.png", width=1000, height=500)
a <- read.csv(file='all-100-all.txt', head=FALSE, sep=",")
a$V3 <- as.factor(a$V3)
a$V4 <- as.factor(a$V4)
a$V7 <- as.factor(a$V7)
ggplot(data=a, aes(x=V3, y=V8, colour=V1, group=V1, linetype=V1)) + geom_line() + geom_point() + xlab("Rotations") + ylab("Height")  + scale_linetype_discrete("Dataset") + scale_colour_discrete("Dataset") + ggtitle("Rotations")
dev.off()