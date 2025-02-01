module lab(
	input clk, sel, reset,
	output [4:1]dOUT
);
reg cntQ1, cntQ2;

cnt_2_10_R(.clk(clk), .dir(1), .reset(reset), .cntQ(cntQ1));

cnt_2_10_R(.clk(clk), .dir(0), .reset(reset), .cntQ(cntQ2));

mux2_1 #(.w(4)) (.dA(cntQ1), .dB(cntQ2), .sel(sel), .muxOUT(dOUT));

endmodule