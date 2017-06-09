
library(Cairo)
library(ggplot2)

Sys.setlocale("LC_ALL", "Portuguese")

CairoPNG(filename="all-100-plot.png", width=750, height=500)
a <- read.csv(file='all-100-all.txt', head=FALSE, sep=",")
a$V3 <- as.factor(a$V3)
a$V4 <- as.factor(a$V4)
a$V7 <- as.factor(a$V7)
ggplot(data=a, aes(x=V3, y=V8, colour=V1, group=V1, linetype=V1)) + geom_line() + geom_point() + xlab("Rotações") + ylab("Ocupação")  + scale_linetype_discrete("Dados") + scale_colour_discrete("Dados") + ggtitle("Comparação 1, 2, 3 e 4 rotações.")
dev.off()