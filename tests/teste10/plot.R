
library(Cairo)
library(ggplot2)

Sys.setlocale("LC_ALL", "Portuguese")

CairoPNG(filename="all-datasets.png", width=750, height=750)
a <- read.csv(file='all-datasets.txt', head=FALSE, sep=",", fileEncoding="UTF-16LE")
a$V2 <- as.factor(a$V2)
ggplot(data=a, aes(x=V4, y=V3, fill=V2)) + geom_col() + xlab("Algoritmo")  + ylab("Ocupação")  + ggtitle("Teste diferentes alturas") +  scale_fill_discrete("Altura") + facet_wrap(~V1+V2, ncol=4)
dev.off()