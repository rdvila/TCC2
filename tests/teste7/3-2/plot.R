
library(Cairo)
library(ggplot2)

Sys.setlocale("LC_ALL", "Portuguese")

CairoPNG(filename="all-datasets.png", width=750, height=750)
a <- read.csv(file='all-datasets.txt', head=FALSE, sep=",", fileEncoding="UTF-16LE")
a$V3 <- as.factor(a$V3)
a$V4 <- as.factor(a$V4)
ggplot(data=a, aes(x=V4, y=V2, fill=V3)) + geom_col() + xlab("Minutes")  + ylab("Height")  + ggtitle("Time test") +  scale_fill_discrete("Time") + facet_wrap(~V1+V3, ncol=3)
dev.off()