module mux2_1 #(parameter w = 2)
(
	input [w:1]dA, dB,
	input sel,
	output reg [w:1]muxOUT
);

always @(*) begin
	if (sel) muxOUT = dA;
	else muxOUT = dB;
end

endmodule