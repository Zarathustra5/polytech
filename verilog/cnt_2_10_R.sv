module cnt_2_10_R
(
  input clk, dir, reset,
  output reg [3:0]cntQ
);

always @(posedge clk or posedge reset) begin
  if (reset) begin
    cntQ <= 4'b0000;
  end else if (dir == 1) begin
    if (cntQ == 4'b1111) cntQ <= 4'b0000;
    else cntQ <= cntQ + 1;
  end else if (dir == 0) begin
    if (cntQ == 4'b0000) cntQ <= 4'b1111;
    else cntQ <= cntQ - 1;
  end
end

endmodule
